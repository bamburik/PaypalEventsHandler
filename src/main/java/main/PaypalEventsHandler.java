package main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class PaypalEventsHandler {


    public static void main(String... args) {
        SpringApplication app = new SpringApplication(PaypalEventsHandler.class);
        Map<String, Object> props = new HashMap<String, Object>();
        props.put("server.port", 8081);
        app.setDefaultProperties(props);
        app.run(args);
    }
}