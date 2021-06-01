package apiserver;

import com.google.gson.JsonObject;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.*;
import io.netty.util.CharsetUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ApiRequestParser extends SimpleChannelInboundHandler<FullHttpMessage> {

    private HttpRequest request; //요청을 처리하기 위한 HttpRequest 변수
    private JsonObject apiResult; //api요청에 따라 업무처리클래스를 호출하고 그 결과를저장할 json객체

    private static final HttpDataFactory factory = new DefaultHttpDataFactory(DefaultHttpDataFactory.MINSIZE);

    private HttpPostRequestDecoder decoder;//사용자가전송한 http 요청의 보문을 추출할 디코더.

    private Map<String,String> reqData = new HashMap<String, String>(); //사용자가전송한 http요청의 파라미터를 업무처리 클래스로 전달하려면 맵 객체를 변수로 등록해야함.
    private static final Set<String> usingHeader = new HashSet<String>();  //클라이언트가 전송한 http헤더중에서 사용할 헤더이름의 목록을 저장.
    static {
        usingHeader.add("token");
        usingHeader.add("email");
    }

    /**
     * 클라이언트가 전송한데이터가 채널 파이프라인의 모든 디코더를 거치고난 뒤에 호출 된다.
     * 메서드 호출에 입력되는 객체는 FullHttpMessage 인터페이스의 구현체고 HTTP 프로토콜의 모든 데이터가 포함되어 있다.
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpMessage msg) throws Exception {
        System.out.println("channelRead0 들어와따!");
        /**
         * HttpRequestDecoder는 HTTP 프로토콜의 데이터를 HttpRequest, HttpContent, LastHttpContent 순서로
         * 디코딩하여 FullHTtpMEssage객체로 만들고 인바운드 이벤트를 발생시킨다.
         * FullHttpMessage 인터페이스는 HttpRequest, HttpContent, LastHttpContent의 최상위인터페이스 이므로
         * 이 코드의 instanceof 연산자는 참을 돌려준다.
         */
        //request header 처리
        if (msg instanceof HttpRequest) {
            this.request = (HttpRequest) msg;


            if (HttpUtil.is100ContinueExpected(request)) {
                send100Continue(ctx); // （3）
            }

            HttpHeaders headers = request.headers(); // 요청의 헤더정보 추출.

            if (!headers.isEmpty()) {
                for (Map.Entry<String, String> h: headers) {
                    String key = h.getKey();
                    if (usingHeader.contains(key)) { reqData.put(key, h.getValue()); } //추룰한 헤더정보에서 usingHEader에 지정된 값만 추출.
                }
            }

            reqData.put("REQUEST_URI", request.uri()); //7
            reqData.put("REQUSET_METHOD", request.method().name()); //8
        } //if HttpRequest end

        //리퀘스트 content 처리.
        if (msg instanceof HttpContent) { //9
            HttpContent httpContent = (HttpContent) msg; //10

            ByteBuf content = httpContent.content(); // 본문데이터 추출.

            if (msg instanceof LastHttpContent) {  // LastHttpContent는 모든 HTTP메시지가 디코딩되었고 HTTP프로토콜의 마지막 데이터임을 알리는 인터페이스이다.
                System.out.println("마지막메세지받았따!! " + request.uri());
                LastHttpContent trailer = (LastHttpContent) msg;
                readPostData(); // 본문에서 HTTP Post 데이터를 추출한다.

                //HTTP프로토콜에서 필요한 데이터 추출이 완료되면 reqData맵을 ServiceDispatcher클래스의 dispatch메서드를 호출하여
                //HTTP 요청에 맞는 api 서비스 클래스를 생성한다.
                ApiRequest service = ServiceDispatcher.dispatch(reqData);

                try {
                    service.executeService();  // dispatch 메서드로부터 생성된 api서비스 클래스 실행.

                    apiResult = service.getApiResult(); // api서비스 클래스의 수행 결과를 apiResult에담음.
                } finally {
                    reqData.clear();
                }

                if (!writeResponse(trailer, ctx)) { // apiResult멤버변수에 저장된 api처리결과를 클라이언트 채널의 송신 버퍼에 기록.
                    ctx.writeAndFlush(Unpooled.EMPTY_BUFFER)
                            .addListener(ChannelFutureListener.CLOSE);
                }
                reset();
            }
        }
    } //channelRead0 end

    private void reset() {
        request = null;
    }

    //
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        System.out.println("요청 처리완료");
        ctx.flush(); //chanelRead0 이벤트 메서드의 수행이 완료된 후에 channelReadComplete 메서드가 호출되고 이때 채널 버퍼의 내용을 클라이언트로 전송한다.
    }

    //HTTP본문 데이터 수신 메서드
    private void readPostData() {
        try {
            decoder = new HttpPostRequestDecoder(factory, request); //HttpRequest 객체에 포함된 HTTP 본문 중에서 POST 메서드로 수신된 데이터를 추출하기위한 디코더생성.

            for (InterfaceHttpData data : decoder.getBodyHttpDatas()) {
                if (InterfaceHttpData.HttpDataType.Attribute == data.getHttpDataType()) {
                    try {
                        Attribute attribute = (Attribute) data;  //2
                        reqData.put(attribute.getName(), attribute.getValue()); //클라이언트가 HTML의 FORM엘리먼트를 사용하여 전송한 데이터를추출.
                        System.out.println("Body Attribute: "+ data.getHttpDataType().name()+ " data : " + data);
                    } catch (IOException e) {
                        System.out.println("Body Attribute: "+ data.getHttpDataType().name() + e);
                        return;
                    }
                } else {
                    System.out.println("BODY data :  "+  data.getHttpDataType().name() + data);
                }
            } //for end

        } catch (HttpPostRequestDecoder.ErrorDataDecoderException e) {
            System.out.println(e.toString());
        } finally {
            if (decoder != null) decoder.destroy();
        }
    } //readPostData end

    private boolean writeResponse(HttpObject currentObj, ChannelHandlerContext ctx) {
        // Decide whether to close the connection or not.
        boolean keepAlive = HttpHeaders.isKeepAlive(request);
        // Build the response object.
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                currentObj.getDecoderResult().isSuccess() ? HttpResponseStatus.OK : HttpResponseStatus.BAD_REQUEST, Unpooled.copiedBuffer(
                apiResult.toString(), CharsetUtil.UTF_8));

        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json; charset=UTF-8");

        if (keepAlive) {
            // Add 'Content-Length' header only for a keep-alive connection.
            response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
            // Add keep alive header as per:
            // -
            // http://www.w3.org/Protocols/HTTP/1.1/draft-ietf-http-v11-spec-01.html#Connection
            response.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
        }

        // Write the response.
        ctx.write(response);

        return keepAlive;
    }

    private static void send100Continue(ChannelHandlerContext ctx) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE);
        ctx.writeAndFlush(response);
    }

}
