package pgdp.net;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public final class TemplateProcessor {

    private final String content;

    public TemplateProcessor(Path templatePath) throws IOException {
        content = Files.readString(templatePath);
    }

    public String replace(Map<String, String> variableAssignments) {
        return variableAssignments.entrySet().stream().reduce(content, (c, e) -> c.replace(e.getKey(), e.getValue()),
                String::concat);
    }
}
