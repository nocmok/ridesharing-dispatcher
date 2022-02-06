package com.nocmok.orp.orp_solver.config.listener;

import com.nocmok.orp.orp_solver.config.JacksonConfig;
import com.nocmok.orp.orp_solver.config.solver.LSSolverConfig;
import com.nocmok.orp.orp_solver.kafka.orp_input.MessageDispatcher;
import com.nocmok.orp.orp_solver.kafka.orp_input.OrpInputRequestHandlers;
import com.nocmok.orp.orp_solver.kafka.orp_input.RequestHandler;
import com.nocmok.orp.orp_solver.kafka.orp_input.dto.MatchVehiclesRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessageDispatcherConfig {

    @Autowired
    private JacksonConfig jacksonConfig;
    @Autowired
    private LSSolverConfig lsSolverConfig;

    @Bean
    public OrpInputRequestHandlers orpInputRequestHandlers(@Autowired MessageDispatcher messageDispatcher) {
        var handler = new OrpInputRequestHandlers(lsSolverConfig.lsSolver());
        messageDispatcher.registerRequestHandler(MatchVehiclesRequest.class, new RequestHandler<>() {
            @Override public void handle(MatchVehiclesRequest requestDto) {
                handler.handleMatchVehicleRequest(requestDto);
            }
        });
        return handler;
    }

    @Bean
    public MessageDispatcher messageDispatcher() {
        var messageDispatcher = new MessageDispatcher(jacksonConfig.objectMapper());
        return messageDispatcher;
    }
}
