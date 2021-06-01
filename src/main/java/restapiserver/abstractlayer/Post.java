package restapiserver.abstractlayer;

import com.google.gson.JsonObject;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;
import restapiserver.HttpChannelHandler;
import restapiserver.NettyHttpUtil;

import java.util.Map;

public class Post extends AbsHttpApi {
    private Post(){}

    private static class lazyHolder {
        private static final Post instance = new Post();
    }

    public static Post getInstance() {
        return lazyHolder.instance;
    }


    @Override
    public String getName() {
        return "post";
    }


    /**
     * post를 key로 1개 조회. ex) 아디가 1인 포스트 조회 /posts/1
     */
    @Override
    public boolean get(HttpRequest msg, HttpChannelHandler handler, ChannelHandlerContext ctx) throws Exception {

        String uri = msg.uri();
        String[] urlArr = uri.split("/");
        String pathVariable = null;

        if (urlArr.length > 2) {
            pathVariable = urlArr[2];
        }
        System.out.println("uri : " + uri);
        System.out.println("pathVariable : " + pathVariable);


        /**
         * 비지니스로직........................
         * 비지니스로직........................
         * 비지니스로직........................
         */

        //결과값 셋팅.
        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("resultCode", "200");
        jsonObj.addProperty("msg","success");
        jsonObj.addProperty("data", "postId: "+pathVariable);


        //채널에 flush
        handler.writeResponseAndFlush(msg, msg, jsonObj, ctx);

        return true;
    }


    @Override
    public boolean post(HttpRequest msg, HttpChannelHandler handler, ChannelHandlerContext ctx) throws Exception {

        //post body에 있는 데이터 가져옴.
        Map<String, String> postData = NettyHttpUtil.readPostData(msg);

        if (!postData.isEmpty()) {
            System.out.println(">>>>>  POST BODY 디버그  <<<<");
            for (Map.Entry<String, String> entry : postData.entrySet()) {
                System.out.println(entry.getKey() + " : " + entry.getValue());
            }
        }
        System.out.println(">>>>>  POST BODY 디버그 끝<<<<");
        /**
         * 비지니스로직........................
         * 비지니스로직........................
         * 비지니스로직........................
         */

        //결과 return
        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("resultCode", "200");
        jsonObj.addProperty("msg","success");
        jsonObj.addProperty("data", "저장됨");

        //채널에 flush
        handler.writeResponseAndFlush(msg, msg, jsonObj, ctx);
        return true;
    }
}
