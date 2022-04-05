package com.nocmok.orp.orp_solver.config.kafka;

import com.nocmok.orp.kafka.orp_input.ServiceRequestMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.CommonLoggingErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;

@EnableKafka
@Configuration
@Slf4j
public class KafkaConsumerConfig {

    @Autowired
    private KafkaConfig kafkaConfig;

    @Value("${kafka.consumers.orp_solver.group_id}")
    private String kafkaOrpSolverConsumerGroupId;

    @Value("${kafka.consumers.orp_solver.n_threads}")
    private Integer nThreads;

    @Bean
    public ConsumerFactory<String, ServiceRequestMessage> orpInputConsumerFactory() {
        var props = new HashMap<String, Object>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.getKafkaBootstrapAddress());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaOrpSolverConsumerGroupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");

        return new DefaultKafkaConsumerFactory<>(props, keyDeserializer(), valueDeserializer());
    }

    private Deserializer<String> keyDeserializer() {
        return new ErrorHandlingDeserializer<>(new StringDeserializer());
    }

    private Deserializer<ServiceRequestMessage> valueDeserializer() {
        return new ErrorHandlingDeserializer<>(new JsonDeserializer<>(ServiceRequestMessage.class));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ServiceRequestMessage> orpInputKafkaListenerContainerFactory() {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, ServiceRequestMessage>();
        factory.setConcurrency(nThreads);
        factory.setConsumerFactory(orpInputConsumerFactory());
        factory.setCommonErrorHandler(new CommonLoggingErrorHandler());
        return factory;
    }
}
