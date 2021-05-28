package echoserver;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * ChannelFutureListner 인터페이스를구현한 클래스를 작성하여 ChannelFuture객체에 등록.
 * 클라이언트소켓채널에데이터 기록이 완료 되었을 때
 * 기록 완료 메세지와 기록된 메시지의 크기를출력하고 소켓 채널을 닫는 이벤트 핸들러
 */
public class EchoServerHandlerWithFuture extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ChannelFuture channelFuture = ctx.writeAndFlush(msg);

        final int writeMessageSize = ( (ByteBuf)msg ).readableBytes();

        channelFuture.addListener((ChannelFutureListener) future -> {
            System.out.println("전송한 Bytes : " + writeMessageSize);
            future.channel().close();
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();;
        ctx.close();
    }
}

