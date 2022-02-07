package com.nocmok.orp.telemetry;

import com.nocmok.orp.telemetry.config.ApplicationConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({
        ApplicationConfig.class
})
public class TelemetryHandlerApplication {

    public static void main(String[] args) {
        SpringApplication.run(TelemetryHandlerApplication.class, args);
    }
}
