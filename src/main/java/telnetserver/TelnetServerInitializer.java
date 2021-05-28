package telnetserver;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class TelnetServerInitializer extends ChannelInitializer<SocketChannel> {

    //아래 두개 인코더와 디코더는연결되는 새로운 클라이언트 채널들이 동일한 인코더/디코더 객체를 공유하게 된다.
    private static final StringDecoder DECODER = new StringDecoder(); //static final, 즉 모든 채널파이프라인에서 공유
    private static final StringEncoder ENCODER = new StringEncoder(); //static final, 즉 모든 채널파이프라인에서 공유

    //텔넷 서버가 제공하는 기능을 구현한 로직이 포함된 TelnetServerHandler를 static final로 설정한다.
    //위의 두 코덱과 마찬가지로 모든 채널 파이프라인에서 공유된다.
    private static final TelnetServerHandler SERVER_HANDLER = new TelnetServerHandler();

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        //DelimiterBasedFrameDecoder의 두 인수는 데이터의 최대크기와 구분자로 구성된다.
        //즉, 수신된데이터의 최대 크기는 8192바이트이고 해당 데이터의마지막은 줄바꿈 문자로 구분된다는 뜻이다.
        pipeline.addLast(new DelimiterBasedFrameDecoder(8192, //첫번재 데이터 핸들러.
                Delimiters.lineDelimiter())); //DelimiterBasedFrameDecoder는 네티가 제공하는 기본 디코더로서 구분자 기반의 패킷을 처리한다.


        pipeline.addLast(DECODER); //전역 상수에 등록된 StringDecoder를 채널 파이프라인의 두번째 데이터 핸들러로 등록.
        pipeline.addLast(ENCODER); //전역 상수에 등록된 StringEncoder를 채널 파이프라인의 세번째데이터 핸들러로 등록.
        pipeline.addLast(SERVER_HANDLER); //TelnetServerHandler를 네번째 데이터 핸들러로 등록.
    }

}
