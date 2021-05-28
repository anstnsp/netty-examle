package echoserver;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

public class EchoClient {
    private final String host;
    private final int port;

    public EchoClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public static void main(String[] args) throws InterruptedException {
        String host = "localhost";
        int port = 25;
        new EchoClient(host, port).start();
    }

    private void start() throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();	//NIO 처리를 다루는 이벤트 루프 인스턴스 생성
        try {
            Bootstrap b = new Bootstrap();				//새로운 클라이언트 채널을 생성하고 연결하기 위한 BootStrap을 생성.
            b.group(group)  //채널의 이벤트 처리를 위한 이벤트루프를 제공하는 이벤트루프그룹을 설정.
                    .channel(NioSocketChannel.class)			// Nio 전송 채널을 사용 하도록 셋팅(이용할 채널 구현을 지정)
                    .remoteAddress(new InetSocketAddress(host, port))
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
//                            ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                            ch.pipeline().addLast(new EchoClientHandler());	//echoserver.EchoClientHandler 을 pipeline으로 설정 한다.
                        }
                    });
            ChannelFuture f = b.connect().sync();	//서버를 비동기 식으로 바인딩 한다. sync() 는 바인딩이 완료되기를 대기한다.
            System.out.println("###### ChannelFuture connect is a success ######");
            /**
             * ChannelFuture 는 작업이 완료되면 그 결과에 접근 할 수 있게 해주는 자리 표시자 역활을 하는 인터페이스
             */
            f.channel().closeFuture().sync();	//채널의 CloseFuture를 얻고 완료 될때 까지 현재 스레드를 블로킹한다.
            System.out.println("###### ChannelFuture has been closed ######");
        }finally {
            group.shutdownGracefully().sync();	//EventLoopGroup 을 종료 하고 모든 리소스를 해제 한다.
        }
    }
}
