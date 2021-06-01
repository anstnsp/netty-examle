package restapiserver.abstractlayer;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import restapiserver.HttpChannelHandler;


abstract public class AbsHttpApi implements IHttpDispatcher {
    abstract public String getName();

    public boolean get(HttpRequest msg, HttpChannelHandler handler, ChannelHandlerContext ctx) throws Exception { return false; }
    public boolean put(HttpRequest msg, HttpChannelHandler handler, ChannelHandlerContext ctx) throws Exception { return false; }
    public boolean post(HttpRequest msg, HttpChannelHandler handler, ChannelHandlerContext ctx) throws Exception { return false; }
    public boolean delete(HttpRequest msg ,HttpChannelHandler handler, ChannelHandlerContext ctx) throws Exception { return false; }


    @Override
    public boolean onDispatcher(String[] path, HttpRequest msg,HttpChannelHandler handler, ChannelHandlerContext ctx) throws Exception {
        HttpRequest request = msg;

        if (HttpMethod.GET.compareTo(request.method()) == 0)
            return get(msg, handler, ctx);
        else if (HttpMethod.POST.compareTo(request.method()) == 0)
            return post(msg, handler, ctx);
        else if (HttpMethod.PUT.compareTo(request.method()) == 0)
            return put(msg, handler, ctx);
        else if (HttpMethod.DELETE.compareTo(request.method()) == 0)
            return delete(msg, handler, ctx);

        return false;
    }

}
