package echoserver;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

import java.time.LocalDateTime;


@ChannelHandler.Sharable  //@Sharable 어노테이션은 여러 채널에서 Handler를 공유 할 수 있음을 나타냄.
public class EchoServerHandler extends ChannelInboundHandlerAdapter {

    /**
     * 메세지가 들어올때마다 호출되는 메소드
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf in = (ByteBuf) msg;
        System.out.println(
                "server received:" + in.toString(CharsetUtil.UTF_8));
        LocalDateTime time = LocalDateTime.now();
        System.out.println("time : " + time);
        ctx.write(in);

//        ChannelFuture channelFuture = ctx.writeAndFlush(msg);
//        channelFuture.addListener(ChannelFutureListener.CLOSE);
    }

    /**
     * channelREad 메소드가 처리완료 되었다는 것을 핸들러에게 통보하는 메소드.
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {

        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER) //대기중인 메시지를 원격피어로 플러시하고 채널을 닫음.
                .addListener(ChannelFutureListener.CLOSE);

    }

    /**
     * 읽기 작업중오류가 발생 했을경우 호출되는 메소드.
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
