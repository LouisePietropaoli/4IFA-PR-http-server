import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.json.JSONObject;

@Data
@NoArgsConstructor
public class Request {
    public enum Method {
        GET("GET"),
        POST("POST"),
        HEAD("HEAD"),
        PUT("PUT"),
        DELETE("DELETE");

        private final String identifier;
        Method(String identifier) {
            this.identifier = identifier;
        }

        public String toString() {
            return this.identifier;
        }

        public static Method getMethodByIdentifier(String identifier) {
            for(Method e : values()) {
                if(e.identifier.equals(identifier)) return e;
            }
            return GET;
        }
    };

    public enum ContentType {
        TEXT("text/plain"),
        JSON("application/json"),
        HTML("text/html"),
        NONE("none");

        private final String identifier;
        ContentType(String identifier) {
            this.identifier = identifier;
        }

        public String toString() {
            return this.identifier;
        }

        public static ContentType getContentTypeByIdentifier(String identifier) {
            for(ContentType e : values()) {
                if(e.identifier.equals(identifier)) return e;
            }
            return NONE;
        }
    };

    private Method method;
    private String path;
    private String version;
    private String host;
    private List<String> headers;
    private String contentType;
    private String status;
    private byte[] content;
    private String body;
}
