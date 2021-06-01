package restapiserver;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

public class RestApiServerInitialzer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();


        pipeline.addLast(new HttpRequestDecoder()); //Http 요청을 처리하는 디코더. 즉 클라이언트가 전송한 http프로토콜을 네티의 바이트 버퍼로 변환작업 수행.
        /**
         * http프로토콜에서 발생하는메시지 파편화를 처리하는 디코더.
         * http 프로토콜을 구성하는 데이터가 나뉘어서 수신되었을 때 데이터를 하나로 합쳐주는 역할을 수행한다.
         * 인자로 입력된 65536은 한꺼번에 처리 가능한 최대 데이터 크기다.
         * 65Kbyte 이상의 데이터가 하나의 http요청으로 수신되면 TooLongFrameException예외가 발생한다.
         */
        pipeline.addLast(new HttpObjectAggregator(65536));
        pipeline.addLast(new HttpResponseEncoder()); //수신된 http요청의 처리결과를 클라이언트로 전송할 때 http프로토콜로 변환해주는 인코더.
        pipeline.addLast(new HttpContentCompressor()); //http프로토콜로 송수신되는 http 본문 데이터를 gzip압축 알고리즘을 사용해 압축과 압축해제 한다.(인바운드,아웃바운드 모두에서 호출)
        //클라이언트로부터 수신된 http데이터에서 헤더와 데이터 값을 추출하여 토큰 발급과 같은 업무
        //처리 클래스로 분기하는 클래스로써 api서버의 컨트롤러 역할을 수행한다.
        pipeline.addLast(new HttpChannelHandler()); //실제 FullHttpMessage 객체의 값을 확인하여 로직 처리 하는 곳.

        //클라이언트로부터 데이터를 수신했을 때데이터 핸들러는
        // HttpRequestDecoder -> HttpObjectAggregator -> HttpContentCompressor => ApiRequestParser
        // 순서로 호출되며
        //ApiRequestParser의 처리가 완료되어 채널로 데이터를 기록할 때 호출되는 데이터핸들러 순서는
        //ApiRequestParser -> HttpContentCompressor -> HttpRespnoseEncoder 순서이다.

    }
}
