package com.nocmok.orp.orp_solver.config.state_keeper;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(com.nocmok.orp.state_keeper.pg.StateKeeperConfig.class)
public class StateKeeperConfig {

}
