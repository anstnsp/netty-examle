package echoserver;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.net.InetSocketAddress;

public class EchoServer {

    private final int port;

    public static void main(String[] args) throws InterruptedException {
        int port = 25;
        new EchoServer(port).start();
    }

    public EchoServer(int port) {
        this.port = port;
    }

    private void start() throws InterruptedException{
        final EchoServerHandler serverHandler = new EchoServerHandler();
        EventLoopGroup bossGroup = new NioEventLoopGroup(1); //클라이언트의 연결을 수락하는 부모 스레드그룹//NIO 처리를 다루는 이벤트루프인스턴스 생성.//숫자쓰면 스레드몇개인지 인수가없으면 하드웨어코어수를기준으로결정cpu코어의2배
        EventLoopGroup workerGroup = new NioEventLoopGroup();  //연결된 클라이언트의 소켓으로부터 데이터 입출력 및 이벤트 처리 담당.
        try {
            /**
             * 그냥 handler는 서버소켓채널에서 발생하는 이벤트를 수신하여 처리. (서버소켓채널의 이벤트핸들러설정)
             * childhandler는 서버에 연결된 클라이언트소켈채널에서 발생하는 이벤트를 수신하여 처리.  (클라이언트소켈채널의 데이터가공핸들러설정)
             */
            ServerBootstrap b = new ServerBootstrap();  //ServerBootstrap 인스턴스 생성. ServerBootstrap은 서버 Channel 을 셋팅 할 수 있는 클래스
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)					// (소켓입출력모드설정) Nio 전송 채널을 사용 하도록 셋팅
                    .localAddress(new InetSocketAddress(port))				// 서버 포트 주소 설정
                    .option(ChannelOption.SO_BACKLOG, 1) //sync received 상태로 변경된 소켓 연결을 갖고 있는 큐의 크기를 설정하는옵션
                    .childHandler(new ChannelInitializer<SocketChannel>() { //연결이 수락될 때마다 호출될 ChannelInitializer 를 지정.
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception { //클라이언트 소켓 채널이 생성될때 자동으로 호출.
                            ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                            ch.pipeline().addLast(serverHandler);			//serverHandler 을 pipeline으로 설정 한다.

                        }
                    });
//                    .childHandler(new ChannelInitializer<SocketChannel>() {
//                        @Override
//                        protected void initChannel(SocketChannel ch) throws Exception {
//                            ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
//                        }
//                    });

            ChannelFuture f = b.bind().sync();	//서버를 비동기 식으로 바인딩 한다. sync() 는 바인딩이 완료되기를 대기한다.
            /**
             * ChannelFuture 는 작업이 완료되면 그 결과에 접근 할 수 있게 해주는 자리 표시자 역활을 하는 인터페이스이다.
             */
            //sync메소드로 인해 바인딩이 완료된 후 아래가 시작되고 , 25번포트에 바인딩된 서버채널을 얻어 올수있다.
            f.channel().closeFuture().sync();	//채널의 CloseFuture를 얻고 완료 될때 까지 현재 스레드를 블로킹한다.
        } finally {
            bossGroup.shutdownGracefully().sync();	//EventLoopGroup 을 종료 하고 모든 리소스를 해제 한다.
            workerGroup.shutdownGracefully().sync();
        }
    }
}
