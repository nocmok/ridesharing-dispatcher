package com.nocmok.orp.orp_solver.config.listener;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        MessageDispatcherConfig.class
})
public class ListenerConfig {

}
