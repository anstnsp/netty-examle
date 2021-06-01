package apiserver;

import java.util.Map;


public class ServiceDispatcher {

    public static ApiRequest dispatch(Map<String, String> requestMap) {
        String serviceUri = requestMap.get("REQUEST_URI");
        String beanName = null;

        if (serviceUri == null) {
            beanName = "notFound";
        }

        if (serviceUri.startsWith("/tokens")) {
            String httpMethod = requestMap.get("REQUEST_METHOD");

            switch (httpMethod) {
                case "POST":
                    beanName = "tokenIssue";
                    break;
                case "DELETE":
                    beanName = "tokenExpier";
                    break;
                case "GET":
                    beanName = "tokenVerify";
                    break;
                default:
                    beanName = "notFound";
                    break;
            } //swtich end
        } else if (serviceUri.startsWith("/users")) {
            beanName = "users";
        } else {
            beanName = "notFound";
        }

        ApiRequest service = null;
        try {
            service = new UserInfo(requestMap);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return service;
    } //dispatch end

}
