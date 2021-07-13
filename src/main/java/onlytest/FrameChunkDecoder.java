package onlytest;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.TooLongFrameException;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class FrameChunkDecoder extends ByteToMessageDecoder {
    private final int maxFrameSize;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int readableBytes = in.readableBytes();//생성할 프레임의 최대 허용 길이를 지정.
        if (readableBytes > maxFrameSize) { //프레임이 너무 큰경우 폐기하고 TooLongFrameException을 생성.
            //바이트를 폐기
            in.clear();
            throw new TooLongFrameException();
        }
        ByteBuf buf = in.readBytes(readableBytes); //그렇지 않은 경우 ByteBuf로 부터 새로운 프레임을 읽음.
        out.add(buf); //디코딩된 메시지의 List에 프레임을 추가.
    }
}
