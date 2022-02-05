package com.nocmok.orp.orp_solver.kafka.orp_input;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

public class MessageDispatcher {

    private final Map<String, Class<?>> requestTypeToDtoClass = new HashMap<>();
    private final Map<String, RequestHandler<?>> requestTypeToHandler = new HashMap<>();
    private ObjectMapper objectMapper;

    public MessageDispatcher(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public <T> void registerRequestHandler(Class<T> requestDtoClass, RequestHandler<T> handlerCallback) {
        requestTypeToDtoClass.put(requestDtoClass.getName(), requestDtoClass);
        requestTypeToHandler.put(requestDtoClass.getName(), handlerCallback);
    }

    public void dispatch(String requestType, String payload) {
        if (!requestTypeToDtoClass.containsKey(requestType)) {
            throw new IllegalArgumentException("illegal request type " + requestType);
        }
        try {
            var payloadDto = objectMapper.readValue(payload, requestTypeToDtoClass.get(requestType));
            requestTypeToHandler.get(requestType)._handle(payloadDto);
        } catch (JsonProcessingException e) {
            // log
            throw new RuntimeException(e);
        }
    }
}
