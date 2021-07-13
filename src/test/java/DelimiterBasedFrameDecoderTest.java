import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import org.junit.jupiter.api.Test;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * 이벤트핸들러테스트를 위해서는 EmbededChannel을 사용함.
 * 이벤트핸들러를 테스트할 수 있는 채널 구현체며 채널의 액션을 에뮬레이트하여 이벤트 핸들러의 동작을 테스트할 수 있다.
 */
public class DelimiterBasedFrameDecoderTest {

    /**
     * DelimiterBasedFrameDecoder를 EmbededChannel에 등록하고
     * 인바운드 채널에 문자열을 기록한 다음 디코딩된 결과를 확인한다.
     */
    @Test
    public void testDecoder() {
        String writeData = "안녕하세요\r\n반갑습니다\r\n";
        String firstResponse = "안녕하세요\r\n";
        String secondResponse = "반갑습니다\r\n";

        //최대 8192바이트의 데이터를 줄바꿈 문자를 기준으로 잘라서 디코딩하는 DelimiterBasedFrameDecoder객체를 생성한다.
        //디코더의 두번째 인수는 디코딩된 데이터에 구분자의 포함 여부를 설정한다.
        //이 예제에서는 false로 하엿으므로 디코딩된 문자열에 줄바꿈 문자가 포함되지 않는다.
        DelimiterBasedFrameDecoder decoder = new DelimiterBasedFrameDecoder(8192,
                false, Delimiters.lineDelimiter());
        EmbeddedChannel embededChannel = new EmbeddedChannel(decoder); //채널에 디코더 객체를 등록.

        ByteBuf request = Unpooled.wrappedBuffer(writeData.getBytes(StandardCharsets.UTF_8)); //문자열을 바이트버퍼로 변환
        boolean result = embededChannel.writeInbound(request); //바이트버퍼를 인바운드에 기록한다. (클라이언트로부터 데이터를수신한것과 같은 상태)
        assertTrue(result); //채널의 버퍼에 데이터가 정상적으로 기록되었으면 true

        ByteBuf response = null;
        //DelimeterBasedFrameDecoder는 줄바꿈 문자를 기준으로 데이터를 분리하므로
        //writeData문자열의 시작부터 첫번째줄바꿈 문자 앞 문자열인 안녕하세요\r\n을 돌려준다.
        response = (ByteBuf) embededChannel.readInbound();//(인바운드데이터를 읽는다)디코더가 수신하여 디코딩한데이터를 조회한다.
        assertEquals(firstResponse, response.toString(Charset.defaultCharset()));

        response = (ByteBuf) embededChannel.readInbound();
        assertEquals(secondResponse, response.toString(Charset.defaultCharset()));

        System.out.println(response.toString(Charset.defaultCharset()));
        System.out.println(response.toString(Charset.defaultCharset()));
        embededChannel.finish();

        /**
         * 전체적인 그림은.. EmbededChannel에 하나의 디코더(DelimeterBasedFrameDecoder)가 등록된 상태
         * 디코더는 인바운드데이터를 가공하는 클래스라서 인바운드로 입력된 데이터에만 반응하기 때문에
         * 디코더의테스트를위해서는 채널의 인바운드로 데이터를 기록해야 한다.
         * 클라이언트접속해서 메세지날림 ->> (embededChannel.writeInbound()
         * 서버가 읽음  ->> embededChannel.readInbound()
         *
         * 디코더만 등록된 채널에는 아웃바운트 이벤트를 발생시키는 writeOutbound(), readOutbound()를 해도 아무런 데이터를 얻을 수 없다.
         * 그러므로 writeOutbound()와 readOutbound()는인코더를 테스트하는데 사용한다.
         */
    }

}
