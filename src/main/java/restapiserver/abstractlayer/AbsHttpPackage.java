package restapiserver.abstractlayer;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;
import restapiserver.HttpChannelHandler;

import java.util.HashMap;

abstract
public class AbsHttpPackage implements IHttpDispatcher {
    private HashMap<String, IHttpDispatcher> dispatcherMap;

    abstract
    public String getName();

    public int getDepth()
    {
        return depth;
    }

    private int depth;

    private AbsHttpPackage parent;

    public AbsHttpPackage(AbsHttpPackage parent) {
        this.parent = parent;
        depth = parent.getDepth() + 1;
        dispatcherMap = getDispatcherMap();
    }

    public AbsHttpPackage() {
        depth = 0;
        dispatcherMap = getDispatcherMap();
    }

    abstract protected HashMap<String, IHttpDispatcher> getDispatcherMap();

    public IHttpDispatcher getChildDispatcher(String apiName)
    {
        return dispatcherMap.get(apiName);
    }

    @Override
    public boolean onDispatcher(String[] path, HttpRequest msg, HttpChannelHandler handler, ChannelHandlerContext ctx) throws Exception {
        IHttpDispatcher dispatcher = getChildDispatcher(path[getDepth() + 1]);

        if (dispatcher != null)
            return dispatcher.onDispatcher(path, msg,handler, ctx);

        return false;
    }

}
