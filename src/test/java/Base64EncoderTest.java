import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.base64.Base64Encoder;
import org.junit.jupiter.api.Test;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * writeOutBound와 readOutBound를 이용한 인코더 테스트.
 */
public class Base64EncoderTest {

    @Test
    public void testEncoder() {
        String writeData = "안녕하세요";
        ByteBuf request = Unpooled.wrappedBuffer(writeData.getBytes(StandardCharsets.UTF_8));

        Base64Encoder encoder = new Base64Encoder(); //테스트를 위한 Base64Encoder 객체를 생성.
        EmbeddedChannel embeddedChannel = new EmbeddedChannel(encoder); //EmbeddedChannel에 Base64Encoder객체를 등록

        embeddedChannel.writeOutbound(request); //writeOutbound메서드로 EmbededChannel의 아웃바운드에 데이터를 기록.
        ByteBuf response = (ByteBuf) embeddedChannel.readOutbound(); //readOutbound메서드로 Base64Encoder의 인코딩결과를 조회한다.

        String expect = "7JWI64WV7ZWY7IS47JqU"; //안녕하세요 의 base64인코딩 값
        assertEquals(expect, response.toString(Charset.defaultCharset()));

    }
}
