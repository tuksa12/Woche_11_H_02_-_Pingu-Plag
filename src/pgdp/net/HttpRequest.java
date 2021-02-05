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
        return HttpMethod.valueOf(firstLine);
    }

    public String getPath(){
        return firstLine;
    }

    public Map<String, String> getParameters(){
        Map<String, String> result = new HashMap<>();
        result.put(firstLine, URLDecoder.decode(body, StandardCharsets.UTF_8));
        return result;
    }
}
