package telnetserver;

import io.netty.channel.*;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Sharable로 지정된 클래스는채널 파이프라인에서 공유할 수 있다.
 * 즉, 다중 스레드에서 경합 없이 참조가 가능하다.
 *
 * SimpleChannelInboundHandler<Type> <<여기에 지정된 타입은 데이터 수신 이벤트인 channelRead0 메서드의
 * 두 번째 인수의 데이터형이 된다. TelnetserverHandler에서 수신된 데이터가 String데이터임을 의미한다.
 */
@ChannelHandler.Sharable
public class TelnetServerHandler extends SimpleChannelInboundHandler<String> {

    /**
     * [클라이언트 접속 완료를 알리는 channelActive 이벤트]
     * channelActive메서드는 채널이 생성된 다음 바로 호출되는 이벤트다.
     * 서버 프로그램을 예로 들면 클라이언트가 서버에 접속되면 네티의 채널이 생성되고 해당 채널이 활성화 되는데 이때 호출된다.
     * 통상적으로 채널이 연결된 직후에 수행할 작업을 처리할 때 사용하는 이벤트다.
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //클라이언트가 처음 접속 했을 때 클라이언트에게 환영메세지 전송.
        ctx.write(InetAddress.getLocalHost().getHostName() +
                " 서버에 접속하셨습니다!\r\n");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime now = LocalDateTime.now();
        ctx.write("현재 시간은 " + now.format(formatter) + " 입니다.\r\n");

        ctx.flush();

    }


    /**
     * [데이터 수신을 알리는 channelRead0 이벤트]
     * channelRead0의 두번째인수의 타입이 String인 이유는 TelnetServerHandler가 String제너릭을 사용한
     * SimpleCHannelInboundHandler를 상속받고 있기 때문이다. >> extends SimpleChannelInboundHandler<String> 부분
     * Simple
     * @param ctx
     * @param request
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String request) throws Exception {
        String response;
        boolean close = false;

        if (request.isEmpty()) {
            response = "명령을 입력해주세요.\r\n";
        } else if ("bye".equals(request.toLowerCase())) {
            //종료 문자열이입력되었으면
            response = "안녕히 가세요!\r\n";
            close = true;
        } else {
            response = "입력하신 명령은 '" + request + "' 입니다.\r\n";
        }

        ChannelFuture future = ctx.write(response);

        if (close) {
            future.addListener(ChannelFutureListener.CLOSE);
        }
    }

    /**
     * [데이터 수신 완료를알리는 channelReadComplete 이벤트]
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();;
        ctx.close();
    }

}
