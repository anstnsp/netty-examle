import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.TooLongFrameException;
import onlytest.FrameChunkDecoder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class FrameChunkDecoderTest {

    @Test
    public void testFramesDecoded() {
        ByteBuf buf= Unpooled.buffer(); //ByteBuf를 생성하고 9바이트를 기록.
        for (int i=0; i<9; i++) {
            buf.writeByte(i);
        }
        /**
         * duplicate()는 복사본을 만드는데 원본객체와 버퍼를 공유한다.
         * 따라서, 원본이나 duplicate()로 인해 생성한값이 서로에게 영향을 준다.
         * readerIndex, writerIndex에 대한 인덱스변화는 없다.
         *
         * copy()는 복사본이지만 서로에게 전혀 영향을 주지 않는 복사본.
         */
        ByteBuf input = buf.duplicate();
        EmbeddedChannel channel = new EmbeddedChannel(new FrameChunkDecoder(3));

        assertTrue(channel.writeInbound(input.readBytes(2))); //2바이트를 기록한 후 새로운 프레임이생성되는지 확인
        /**
         * 여기에사용된 try/catch블록은 EmbeddedChannel의 특수 기능으로서, write* 메서드 중 하나에서 확인된 Exception이
         * 발생하는경우 이 Exception이 RuntimeException에 래핑된다.
         * 이 방법으로 데이터를 처리하는 동안 Exception이 처리됐는지 여부를 쉽게 테스트할 수 있다.
         */
        try {
            channel.writeInbound(input.readBytes(4));
//            Assert.error(); //예외가 발생하지 않은 경우 이 어설션이적용되고 테스트가 실패한다.
        } catch(TooLongFrameException e) {
            //예상된 예외
            System.out.println(e);
        }
        assertTrue(channel.writeInbound(input.readBytes(3))); //남은 3바이트를 기록하고 프레임이 올바른지 확인.
        assertTrue(channel.finish()); //채널을 완료로 표시

        //프레임을 읽음.
        ByteBuf read = (ByteBuf) channel.readInbound();
        assertEquals(buf.readSlice(2), read);
        read.release();

        read = (ByteBuf) channel.readInbound();
        assertEquals(buf.skipBytes(4).readSlice(3), read);
        read.release();
        buf.release();

    }
}
