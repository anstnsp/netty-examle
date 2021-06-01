package apiserver;

import com.google.gson.JsonObject;

import java.util.Map;

abstract public class ApiRequestTemplate implements ApiRequest {

    protected final Map<String,String> reqData;
    protected final JsonObject apiResult;

    public ApiRequestTemplate(Map<String,String> reqData) {
        this.reqData = reqData;
        this.apiResult = new JsonObject();
    }

    @Override
    public void executeService() {
        this.requestParamValidation();
        this.service();
    }


    @Override
    public JsonObject getApiResult() {
        return this.apiResult;
    }


}
