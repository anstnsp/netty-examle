package restapiserver.abstractlayer;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;
import restapiserver.HttpChannelHandler;


public interface IHttpDispatcher
{

    public String getName();

    public boolean onDispatcher(String[] path, HttpRequest msg, HttpChannelHandler handler, ChannelHandlerContext ctx) throws Exception;
}
