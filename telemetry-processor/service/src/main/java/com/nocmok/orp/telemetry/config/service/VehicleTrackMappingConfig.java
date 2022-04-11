package com.nocmok.orp.telemetry.config.service;

import com.nocmok.orp.graph.api.SpatialGraphUtils;
import com.nocmok.orp.telemetry.tracker.VehicleTrackMappingStrategy;
import com.nocmok.orp.telemetry.tracker.VotingTrackMappingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VehicleTrackMappingConfig {

    @Bean
    @Autowired
    public VehicleTrackMappingStrategy vehicleTrackMappingStrategy(SpatialGraphUtils graphUtils) {
        return new VotingTrackMappingStrategy(graphUtils, 1, 1, 3, 0.7);
    }
}
