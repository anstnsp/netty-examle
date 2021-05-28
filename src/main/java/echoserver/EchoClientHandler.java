package echoserver;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

@ChannelHandler.Sharable //Sharable 어토네이션은 여러 채널에서 Handler를 공유 할 수 있음을나타냄.
public class EchoClientHandler extends SimpleChannelInboundHandler<ByteBuf> {

    /**
     * 서버로 연결이만들어 지면 channelActive 메소드가 호출된다.
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(Unpooled.copiedBuffer("Netty rocks!", CharsetUtil.UTF_8));
    }

    /**
     * 서버에서 메시지를 수신 하면 channelRead0 메소드가 호출 된다.
     * @param ctx
     * @param in
     * @throws Exception
     */
    @Override
    public void channelRead0(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        System.out.println("client received: " + in.toString(CharsetUtil.UTF_8));
    }

    /**
     * 예외 발생 시 exceptionCaught 메소드가 호출된다.
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }



}
