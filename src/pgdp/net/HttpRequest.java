package pgdp.net;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public final class HttpRequest {
    String firstLine;
	String body;

    public HttpRequest(String firstLine, String body) {
        this.firstLine = firstLine;
        this.body = body;
    }

    public HttpMethod getMethod(){
        return HttpMethod.valueOf(firstLine.substring(0,firstLine.indexOf(" ")));
    }

    public String getPath(){
        return firstLine.substring(firstLine.indexOf(" ")+1,firstLine.indexOf('?'));
    }

    public Map<String, String> getParameters(){
        Map<String, String> result = new HashMap<>();

        boolean containsParameters = firstLine.contains("=");
        String parametersFirstLine = firstLine.substring(firstLine.indexOf("?") +1, firstLine.lastIndexOf(" "));
        while(containsParameters){
            if (parametersFirstLine.contains("&")){
                String parameter = parametersFirstLine.substring(0,parametersFirstLine.indexOf('&'));
                result.put(URLDecoder.decode(parameter.substring(0,parameter.indexOf("=")),StandardCharsets.UTF_8),URLDecoder.decode(parameter.substring(parameter.indexOf("=") +1),StandardCharsets.UTF_8));
                parametersFirstLine = parametersFirstLine.substring(parametersFirstLine.indexOf('&') + 1);
            } else {
                String parameter = parametersFirstLine;
                result.put(URLDecoder.decode(parameter.substring(0,parameter.indexOf("=")),StandardCharsets.UTF_8),URLDecoder.decode(parameter.substring(parameter.indexOf("=") +1),StandardCharsets.UTF_8));
                containsParameters = false;
            }
        }

        containsParameters = body.contains("=");
        String parametersBody = body;
        while(containsParameters){
            if (parametersBody.contains("&")){
                String parameter = parametersBody.substring(0,parametersBody.indexOf('&'));
                result.put(URLDecoder.decode(parameter.substring(0,parameter.indexOf("=")),StandardCharsets.UTF_8),URLDecoder.decode(parameter.substring(parameter.indexOf("=") +1),StandardCharsets.UTF_8));
                parametersBody = parametersBody.substring(parametersBody.indexOf('&') + 1);
            } else {
                String parameter = parametersBody;
                result.put(URLDecoder.decode(parameter.substring(0,parameter.indexOf("=")),StandardCharsets.UTF_8),URLDecoder.decode(parameter.substring(parameter.indexOf("=") +1),StandardCharsets.UTF_8));
                containsParameters = false;
            }
        }
        return result;
    }
}
