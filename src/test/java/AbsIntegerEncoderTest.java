import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import onlytest.AbsIntegerEncoder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AbsIntegerEncoderTest {

    @Test
    public void testEncoded() {
        //ByteBuf를 생성하고 음의 정수 9개를 기록.
        ByteBuf buf = Unpooled.buffer();
        for (int i=1; i<10; i++) {
            buf.writeInt(i * -1);
        }
        //EmbededChannel을 생성하고 테스트할 AbsIntegerEncoder 추가
        EmbeddedChannel channel = new EmbeddedChannel(new AbsIntegerEncoder());

        assertTrue(channel.writeOutbound(buf)); //ByteBuf를 채널에 기록하고 readOutBound()가 데이터를 생성하는지 확인.
        assertTrue(channel.finish()); //채널을 완료로 표시

        //바이트를 읽음.
        for (int i=1; i<10; i++) {
            assertEquals(i, (int) channel.readOutbound());
        }
        assertNull(channel.readOutbound());

    }
}
