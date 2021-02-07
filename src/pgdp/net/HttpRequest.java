package pgdp.net;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public final class HttpRequest {
    //Attributes
    String firstLine;
	String body;

	//Constructor
    public HttpRequest(String firstLine, String body) {
        this.firstLine = firstLine;
        this.body = body;
    }

    //Returns the value of the request
    public HttpMethod getMethod(){
        try{
            return HttpMethod.valueOf(firstLine.substring(0,firstLine.indexOf(" ")));
        } catch (Exception e){
            throw new InvalidRequestException("Error: Invalid request method");
        }
    }

    //Returns the Path as string
    public String getPath(){
        return firstLine.substring(firstLine.indexOf(" ")+1,firstLine.indexOf('?'));
    }

    //Returns the parameters of the request
    public Map<String, String> getParameters(){
        Map<String, String> result = new HashMap<>();

        try{
            boolean containsParameters = firstLine.contains("=");//boolean that shows if there is any parameters in the first line
            String parametersFirstLine = firstLine.substring(firstLine.indexOf("?") +1, firstLine.lastIndexOf(" "));
            while(containsParameters){//Loop to find the parameters in the first line
                if (parametersFirstLine.contains("&")){//In case there are more than one parameter
                    String parameter = parametersFirstLine.substring(0,parametersFirstLine.indexOf('&'));
                    result.put(URLDecoder.decode(parameter.substring(0,parameter.indexOf("=")),StandardCharsets.UTF_8),URLDecoder.decode(parameter.substring(parameter.indexOf("=") +1),StandardCharsets.UTF_8));
                    parametersFirstLine = parametersFirstLine.substring(parametersFirstLine.indexOf('&') + 1);
                } else {
                    String parameter = parametersFirstLine;
                    result.put(URLDecoder.decode(parameter.substring(0,parameter.indexOf("=")),StandardCharsets.UTF_8),URLDecoder.decode(parameter.substring(parameter.indexOf("=") +1),StandardCharsets.UTF_8));
                    containsParameters = false;
                }
            }

            containsParameters = body.contains("=");//Same as in the firstline
            String parametersBody = body;
            while(containsParameters){//Loop to find the parameters in the body
                if (parametersBody.contains("&")){//In case there are more than one parameter
                    String parameter = parametersBody.substring(0,parametersBody.indexOf('&'));
                    result.put(URLDecoder.decode(parameter.substring(0,parameter.indexOf("=")),StandardCharsets.UTF_8),URLDecoder.decode(parameter.substring(parameter.indexOf("=") +1),StandardCharsets.UTF_8));
                    parametersBody = parametersBody.substring(parametersBody.indexOf('&') + 1);
                } else {
                    String parameter = parametersBody;
                    result.put(URLDecoder.decode(parameter.substring(0,parameter.indexOf("=")),StandardCharsets.UTF_8),URLDecoder.decode(parameter.substring(parameter.indexOf("=") +1),StandardCharsets.UTF_8));
                    containsParameters = false;
                }
            }
        }catch (Exception E){//Any exceptions, throws a InvalidRequestException
            throw new InvalidRequestException("Error: Invalid request, could not read the parameters");
        }
        return result;
    }
}
