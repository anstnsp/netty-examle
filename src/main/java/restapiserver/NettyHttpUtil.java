package restapiserver;

import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NettyHttpUtil {

    //HTTP본문 데이터 수신 메서드
    public static Map<String, String> readPostData(HttpRequest request) {
        HttpPostRequestDecoder decoder = null;
        Map<String, String>    result  = new HashMap<>();

        try {
            decoder = new HttpPostRequestDecoder(new DefaultHttpDataFactory(false), request); //HttpRequest 객체에 포함된 HTTP 본문 중에서 POST 메서드로 수신된 데이터를 추출하기위한 디코더생성.

            for (InterfaceHttpData data : decoder.getBodyHttpDatas()) {
                if (InterfaceHttpData.HttpDataType.Attribute == data.getHttpDataType()) {
                    try {
                        Attribute attribute = (Attribute) data;  //2
                        result.put(attribute.getName(), attribute.getValue()); //클라이언트가 HTML의 FORM엘리먼트를 사용하여 전송한 데이터를추출.
                        System.out.println("Body Attribute: "+ data.getHttpDataType().name()+ " data : " + data);
                    } catch (IOException e) {
                        System.out.println("Body Attribute: "+ data.getHttpDataType().name() + e);
                    }
                } else {
                    System.out.println("BODY data :  "+  data.getHttpDataType().name() + data);
                }
            } //for end

        } catch (HttpPostRequestDecoder.ErrorDataDecoderException e) {
            System.out.println(e.toString());
        } finally {
            if (decoder != null) decoder.destroy();
        }
        return result;
    } //readPostData end


    public static Map<String, List<String>> readHttpParam(HttpRequest request) {
        QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.uri());
        Map<String, List<String>> params = queryStringDecoder.parameters();
        return params;
    } //readHttpParam end



}
