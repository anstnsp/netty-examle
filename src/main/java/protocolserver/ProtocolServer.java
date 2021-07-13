//package protocolserver;
//
//import io.netty.bootstrap.ServerBootstrap;
//import io.netty.channel.*;
//import io.netty.channel.nio.NioEventLoopGroup;
//import io.netty.channel.socket.SocketChannel;
//import io.netty.channel.socket.nio.NioServerSocketChannel;
//import io.netty.handler.logging.LogLevel;
//import io.netty.handler.logging.LoggingHandler;
//
//public class ProtocolServer {
//    private static final int serverPort = 9070;
//
//    public static void main(String[] args) {
//        final EventLoopGroup bossGroup  = new NioEventLoopGroup(configInfo.getBossCount());
//        final EventLoopGroup workerGroup  = new NioEventLoopGroup(configInfo.getWorkerCount());
//
//        try {
//            final ServerBootstrap b = new ServerBootstrap();
//            b.option(ChannelOption.SO_BACKLOG, 100);
//            b.option(ChannelOption.SO_REUSEADDR, true);
//            b.option(ChannelOption.TCP_NODELAY, true);
//            b.option(ChannelOption.SO_KEEPALIVE, true);
//
//            b.group(bossGroup, workerGroup)
//                    .channel(NioServerSocketChannel.class)                              //서버 소켓 입출력 모드를 NIO로 설정
//                    .handler(new LoggingHandler(LogLevel.INFO))                         //서버 소켓 채널 핸들러 등록
//                    .childHandler(new ChannelInitializer<SocketChannel>() {             //송수신 되는 데이터 가공 핸들러
//                        @Override
//                        protected void initChannel(SocketChannel ch) {
//                            ChannelPipeline pipeline = ch.pipeline();
//                            pipeline.addLast(new LoggingHandler(LogLevel.DEBUG));
//                            pipeline.addLast(new NicpDecoder());
//                            pipeline.addLast(nicpHandler);
//                        }
//                    });
//            final ChannelFuture channelFuture = b.bind(configInfo.getTcpPort()).sync();
//            channelFuture.channel().closeFuture().sync();
//        } catch (InterruptedException e) {
//            System.out.println(e.getMessage());
//            logger.error(e.getMessage());
//        } finally {
//            bossGroup.shutdownGracefully();
//            workerGroup.shutdownGracefully();
//        }
//    }
//
//
//    @Override
//    public void run() {
//        logger.info("DaishinMiddleTcpServer");
//        final EventLoopGroup bossGroup  = new NioEventLoopGroup(configInfo.getBossCount());
//        final EventLoopGroup workerGroup  = new NioEventLoopGroup(configInfo.getWorkerCount());
//
//        try {
//            final ServerBootstrap b = new ServerBootstrap();
//            b.option(ChannelOption.SO_BACKLOG, 100);
//            b.option(ChannelOption.SO_REUSEADDR, true);
//            b.option(ChannelOption.TCP_NODELAY, true);
//            b.option(ChannelOption.SO_KEEPALIVE, true);
//
//            b.group(bossGroup, workerGroup)
//                    .channel(NioServerSocketChannel.class)                              //서버 소켓 입출력 모드를 NIO로 설정
//                    .handler(new LoggingHandler(LogLevel.INFO))                         //서버 소켓 채널 핸들러 등록
//                    .childHandler(new ChannelInitializer<SocketChannel>() {             //송수신 되는 데이터 가공 핸들러
//                        @Override
//                        protected void initChannel(SocketChannel ch) {
//                            ChannelPipeline pipeline = ch.pipeline();
//                            pipeline.addLast(new LoggingHandler(LogLevel.DEBUG));
//                            pipeline.addLast(new NicpDecoder());
//                            pipeline.addLast(nicpHandler);
//                        }
//                    });
//            final ChannelFuture channelFuture = b.bind(configInfo.getTcpPort()).sync();
//            channelFuture.channel().closeFuture().sync();
//        } catch (InterruptedException e) {
//
//            logger.error(e.getMessage());
//        } finally {
//            bossGroup.shutdownGracefully();
//            workerGroup.shutdownGracefully();
//        }
//    }
//
//}
