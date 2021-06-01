package restapiserver;

import com.google.gson.JsonObject;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.net.URI;
import java.util.Map;

public class HttpChannelHandler extends SimpleChannelInboundHandler<FullHttpMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpMessage msg) throws Exception {
        //request header 처리
        if (msg instanceof HttpRequest) {
            HttpRequest request = (HttpRequest) msg;


            if (HttpUtil.is100ContinueExpected(request)) {
                send100Continue(ctx); // （3）
            }

            HttpHeaders headers = request.headers(); // 요청의 헤더정보 추출.
            System.out.println("=========헤더 값============");
            if (!headers.isEmpty()) {
                for (Map.Entry<String, String> h: headers) {
                    String key = h.getKey();
                    System.out.println(key + " : " + h.getValue());
                }
            }
            System.out.println("=========헤더 끝============");

            //리퀘스트 content 처리.
            if (msg instanceof HttpContent) { //9
                HttpContent httpContent = (HttpContent) msg; //10

                ByteBuf content = httpContent.content(); // 본문데이터 추출.
                System.out.println("content : " + content);

                if (msg instanceof LastHttpContent) {  // LastHttpContent는 모든 HTTP메시지가 디코딩되었고 HTTP프로토콜의 마지막 데이터임을 알리는 인터페이스이다.
                    System.out.println("LastHttpContent =>> "+ msg);
                    LastHttpContent trailer = (LastHttpContent) msg;

                    try {
                        //url에 대해 dispatch
                        if (!RootPackage.getInstance().onDispatcher(new URI(request.uri()).getPath().split("/"), request, this, ctx)) {

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } //if end
            } //if end


        } //if (msg instanceof HttpRequest)  end
    } //channelRead0 end

        @Override
        public void channelReadComplete (ChannelHandlerContext ctx) throws Exception {
            System.out.println("요청 처리완료");
            ctx.flush(); //chanelRead0 이벤트 메서드의 수행이 완료된 후에 channelReadComplete 메서드가 호출되고 이때 채널 버퍼의 내용을 클라이언트로 전송한다.
        }


        public void writeResponseAndFlush (HttpObject currentObj, HttpRequest request, JsonObject apiResult, ChannelHandlerContext ctx){
            // Decide whether to close the connection or not.
            boolean keepAlive = HttpUtil.isKeepAlive(request);
            // Build the response object.

            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                    currentObj.decoderResult().isSuccess() ? HttpResponseStatus.OK : HttpResponseStatus.BAD_REQUEST, Unpooled.copiedBuffer(
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
            if (!keepAlive) {
                ctx.writeAndFlush(Unpooled.EMPTY_BUFFER)
                        .addListener(ChannelFutureListener.CLOSE);
            }
//            return keepAlive;
        } //writeResponse end


        private void send100Continue (ChannelHandlerContext ctx){
            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE);
            ctx.writeAndFlush(response);
        } //send100Continue end

} //class




