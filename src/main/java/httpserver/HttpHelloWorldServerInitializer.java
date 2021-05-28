package httpserver;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.SslContext;

public class HttpHelloWorldServerInitializer extends ChannelInitializer<SocketChannel> {

    private final SslContext sslCtx;

    public HttpHelloWorldServerInitializer(SslContext sslCtx) {
        this.sslCtx = sslCtx;
    }


    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline p = ch.pipeline();
        if (sslCtx != null) {
            p.addLast(sslCtx.newHandler(ch.alloc()));
        }
        /**
         * 네티가 지원하는 HTTP서버 코덱(HttpServerCodec)
         * 이코덱은 인바운드와 아웃바운드 핸들러를 모두 구현한다.
         * 간단한 웹서버를 생성하는데 사용되는 코덱으로서 수신된 ByteBuf 객체를 HttpRequest 와 HtpContent 객체로 변환하고
         * HttpResponse 객체를 ByteBuf 로 인코딩하여송신한다.
         */
        p.addLast(new HttpServerCodec()); //HttpServerCodec은 인바운드와 아웃바운드 이벤트 핸들러를 모두 구현한다.
        p.addLast(new HttpHelloWorldServerHandler());

    }
}
