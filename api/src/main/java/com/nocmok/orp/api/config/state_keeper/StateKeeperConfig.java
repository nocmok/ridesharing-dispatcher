package com.nocmok.orp.api.config.state_keeper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nocmok.orp.state_keeper.api.StateKeeper;
import com.nocmok.orp.state_keeper.pg.StateKeeperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class StateKeeperConfig {

    @Bean
    @Autowired
    public StateKeeper<?> stateKeeper(DataSource dataSource, ObjectMapper objectMapper) {
        return new StateKeeperImpl(dataSource, objectMapper);
    }

}
