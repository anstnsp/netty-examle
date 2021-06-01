package apiserver;


import java.util.Map;

public class UserInfo extends ApiRequestTemplate {

    UserInfo(Map<String, String> reqData) {
        super(reqData);
    }


    @Override
    public void requestParamValidation() {
        if(this.reqData.get("email").isEmpty()) {
            throw new IllegalArgumentException("email이 없습니다");
        }
    }

    @Override
    public void service() {
        //조회 결과...

        String userNo = "no1";
        this.apiResult.addProperty("resultCode", "200");
        this.apiResult.addProperty("message", "Success");
        this.apiResult.addProperty("userNo", userNo);
    }

}
