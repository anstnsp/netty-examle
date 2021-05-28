package telnetserver;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class TelnetServer {
    private static final int listenPort = 8888;

    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO)) //서버쪽 로그 핸들러 추가.
                    .childHandler(new TelnetServerInitializer()); //텔넷 서버의 채널 파이프라인 설정 코드가 작성된 클래스를 부트스트랩에 지정.
            /**
             * 부트스트랩에 텔넷 서비스 포트를 지정하고 서버 소켓에 바인딩된 채널이 종료될 때까지 대기하도록 설정한다.
             * 여기서 ChannelFuture 인터페이스의 sync메서드는 지정한 Future객체의 동작이 완료될 때까지 대기하는 메서드다.
             */
            b.bind(listenPort).sync().channel().closeFuture().sync();
            /**
             * 위를 아래처럼 바꾸기 가능.
             * ChannelFuture future = b.bind(listenPort).sync; -- A
             * future.channel().closeFuture().sync();  -- B
             *
             * sync 메소드는 네티가 제공하는 promise패턴의 구현체인 ChannelFuture클래스의 메소드로서
             * 비동기 메소드의 결과가 완료될 때까지 블로킹한다.
             * 즉, 위의 A가 완료되야 B가 실행된다.
             */
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
