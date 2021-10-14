import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.jupiter.api.Test;
import telnetserver.TelnetServerHandler;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;


/**
 * 텔넷서버핸들러는 channelActive이벤트메서드에서 아웃바운드로 데이터를 전송하는 write메서드를 사용하고 있다.
 * 즉 텔넷서버핸들러는 클라이언트가 연결되거나 데이터를 수신하면 즉시 아웃바운드로 데이터를 전송하고 다음 이벤트 핸들러로
 * 이벤트를넘기지 않으므로 데이터를 기록하는데는 writeInboud를 사용하고 처리결과를 확인하는 데는
 * readOutboud메서드를 사용해야한다.
 */
public class TelnetServerHandlerV3Test {

    @Test
    public void testConnect() {
        StringBuilder builder1 = new StringBuilder();
        StringBuilder builder2 = new StringBuilder();
        try {
            //빌더1,2는 텔넷서버에 접속했을 때 출력하는 메세지를 생성한다. ctx.write() 에 쓰이는 메세지
            builder1
                    .append(InetAddress.getLocalHost().getHostName())
                    .append(" 서버에 접속하셨습니다!\r\n");
            builder2.append("sdf현재 시간은 ")
                    .append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                    .append(" 입니다.\r\n");

        } catch (UnknownHostException e) {
            fail();
            e.printStackTrace();
        }

        //텔넷서버핸들러 객체를 채널에 등록.
        EmbeddedChannel embeddedChannel = new EmbeddedChannel(new TelnetServerHandler());

        /**
         * 텔넷서버핸들러는 클라이언트가 접속할 때마다 환영메세지를 전송한다.
         * 인바운드 이벤트 핸들러의 channelActive 이벤트 메서드는 이벤트 핸들러가 EmbededChannel에 등록될 때 호출된다.
         * 그러므로 다른 write이벤트 메서드호출없이 readOutbound 메서드로 아웃바운드데이터를 조회할 수 있따.
         */
        String expected = (String) embeddedChannel.readOutbound(); //하나의 ctx.write()당 하나의 embeddedChannel.readOutbound()
        assertNotNull(expected);
        assertEquals(builder1.toString(), (String) expected);

        expected = (String) embeddedChannel.readOutbound(); //하나의 ctx.write()당 하나의 embeddedChannel.readOutbound()
        assertNotNull(expected);
        assertEquals(builder2.toString(), (String) expected);

        String request = "hello";
        expected = "입력하신 명령은 '" + request + "' 입니다.\r\n";

        embeddedChannel.writeInbound(request);

        String response = (String) embeddedChannel.readOutbound();
        assertEquals(expected, response);

        embeddedChannel.finish();

        //새로운 이벤트 핸들러를작성할때는반드시 EmbeddedChannel 클래스를 사용하여 테스트케이스를 작성하자!.
    }

}
