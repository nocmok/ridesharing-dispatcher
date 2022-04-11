package com.nocmok.orp.orp_solver.config.state_keeper;

import com.nocmok.orp.orp_solver.config.jackson.JacksonConfig;
import com.nocmok.orp.orp_solver.config.postgres.PostgressConfig;
import com.nocmok.orp.state_keeper.pg.StateKeeperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StateKeeperConfig {

    @Autowired
    private JacksonConfig jacksonConfig;
    @Autowired
    private PostgressConfig postgressConfig;

    @Bean
    public StateKeeperImpl stateKeeper() {
        return new StateKeeperImpl(postgressConfig.datasource(), jacksonConfig.objectMapper());
    }
}
