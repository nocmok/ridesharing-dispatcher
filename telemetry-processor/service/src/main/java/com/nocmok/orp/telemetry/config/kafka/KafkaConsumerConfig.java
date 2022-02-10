package com.nocmok.orp.telemetry.config.kafka;

import com.nocmok.orp.telemetry.kafka.orp_telemetry.dto.VehicleTelemetryMessage;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;

@EnableKafka
@Configuration
public class KafkaConsumerConfig {

    @Autowired
    private KafkaConfig kafkaConfig;

    @Value("${kafka.consumers.orp_telemetry.group_id}")
    private String kafkaOrpTelemetryConsumerGroupId;

    @Value("${kafka.consumers.orp_telemetry.concurrency}")
    private Integer concurrency;

    @Value("${quartz.orp.actuator.refresh_interval}")
    private Integer refreshInterval;

    @Bean
    public ConsumerFactory<String, VehicleTelemetryMessage> orpTelemetryConsumerFactory() {
        var props = new HashMap<String, Object>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.getKafkaBootstrapAddress());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaOrpTelemetryConsumerGroupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 10000);
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 1000);

        return new DefaultKafkaConsumerFactory<>(props,
                new ErrorHandlingDeserializer<>(new StringDeserializer()),
                new ErrorHandlingDeserializer<>(new JsonDeserializer<>(VehicleTelemetryMessage.class)));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, VehicleTelemetryMessage> orpTelemetryKafkaListenerContainerFactory() {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, VehicleTelemetryMessage>();
        factory.setBatchListener(true);
        factory.setConcurrency(concurrency);
        factory.setConsumerFactory(orpTelemetryConsumerFactory());
        factory.getContainerProperties().setIdleBetweenPolls(refreshInterval);

//        TODO Добавить обработку ошибок
//        factory.setErrorHandler();
//        factory.setBatchErrorHandler();

        return factory;
    }
}
