package httpserver;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.util.AsciiString;

/**
 * HttpServerCoden으로부터 수신된 channelRead이벤트를 처리해야 하므로 ChannelInboundHandlerAdpater 추상클래스를 상속한다.
 * HTTPServerCodec으로부터 수신된 HTTP 데이터에 대한 처리를 수행하는 핸들러
 */
public class HttpHelloWorldServerHandler extends ChannelInboundHandlerAdapter {
    private static final byte[] CONTENT = {  //웹브라우저로 전송할 메세지를 상수로 선언.
            'H','e', 'l', 'l', 'o', 'w', 'o', 'r', 'l', 'd'
    };
    private static final AsciiString CONTENT_TYPE = new AsciiString("Content-Type");
    private static final AsciiString CONTENT_LENGTH = new AsciiString("Content-Length");
    private static final AsciiString CONNECTION = new AsciiString("Connnection");
    private static final AsciiString KEEP_ALIVE = new AsciiString("keep-alive");


    /**
     * 웹브라우저로부터 데이터가 모두 수신되었을 때 채널 버퍼의 내용을 웹브라우저로 전송한다.
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();  //웹브라우저로부터 데이터가 모두 수신되었을 때 채널 버퍼의 내용을 웹브라우저로 전송한다.
    }

    /**
     * channelRead이벤트로 수신되는 http객체는 httprequest, httpmessage, LastHttpContent로 구분된다.
     * HttpServerCodec으로부터 수신된 channelRead이벤트를 처리하려면 channelRead오버라이드 한다.
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HttpRequest) { //수신된 객체가 HttpRequest일때 HttpResponse객체를 생성하고 헤더와 메세지를 저장.
            HttpRequest req = (HttpRequest) msg;

            if (HttpHeaders.is100ContinueExpected(req)) {
                ctx.write(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1 ,HttpResponseStatus.CONTINUE));
            }
            boolean keepAlive = HttpHeaders.isKeepAlive(req);
            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
                    Unpooled.wrappedBuffer(CONTENT));
            response.headers().set(CONTENT_TYPE, "text/plan");
            response.headers().set(CONTENT_LENGTH, response.content().readableBytes());

            if (!keepAlive) {
                ctx.write(response).addListener(ChannelFutureListener.CLOSE);
            } else {
                response.headers().set(CONNECTION, KEEP_ALIVE);
                ctx.write(response);
            }

        } // if ( msg instanceof HttpReqest )
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

}
