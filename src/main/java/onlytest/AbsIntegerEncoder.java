package onlytest;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

public class AbsIntegerEncoder extends MessageToMessageEncoder<ByteBuf> {
    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {

        while (msg.readableBytes() >= 4) { //인코딩할 바이트가 충분한지 확인.
            int value = Math.abs(msg.readInt()); //ByteBuf에서 다음 int를 읽고 절대값 계산
            out.add(value); //int를 인코딩된 메세지의 List에 기록
        }

    }

}
