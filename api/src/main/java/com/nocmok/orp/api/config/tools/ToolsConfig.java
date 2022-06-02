package com.nocmok.orp.api.config.tools;

import com.nocmok.orp.graph.tools.GPSTrackLengthCalculator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ToolsConfig {

    private Double outlineVelocityThreshold = 1000d;
    private Double zeroVelocityThreshold = 0.1d;

    @Bean
    public GPSTrackLengthCalculator gpsTrackLengthCalculator() {
        return new GPSTrackLengthCalculator(outlineVelocityThreshold, zeroVelocityThreshold);
    }
}
