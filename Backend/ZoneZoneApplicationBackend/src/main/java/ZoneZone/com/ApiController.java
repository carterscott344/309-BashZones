package ZoneZone.com;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple API Controller that returns basic server information.
 */
@RestController
@RequestMapping("/api")
public class ApiController {

    @GetMapping("/info")
    public Map<String, Object> serverInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("server", "ZoneZone Backend");
        info.put("version", "1.0-SNAPSHOT");
        info.put("status", "Online");
        info.put("swaggerDocs", "/swagger-ui-custom.html");
        info.put("apiDocs", "/api-docs");

        return info;
    }
}
