package restapiserver;

import restapiserver.abstractlayer.AbsHttpPackage;
import restapiserver.abstractlayer.IHttpDispatcher;
import restapiserver.abstractlayer.Post;

import java.util.HashMap;

public class RootPackage extends AbsHttpPackage
{

    private RootPackage(){}
    private static class lazyHolder {
        private static final RootPackage instance = new RootPackage();
    }

    public static RootPackage getInstance() {
        return lazyHolder.instance;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    protected HashMap<String, IHttpDispatcher> getDispatcherMap() {
        HashMap<String, IHttpDispatcher> apiMap = new HashMap<String, IHttpDispatcher>();
        apiMap.put(Post.getInstance().getName(), Post.getInstance());

        return apiMap;
    }
}
