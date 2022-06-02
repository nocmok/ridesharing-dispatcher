package com.nocmok.orp.notifier.config.kafka;

import com.nocmok.orp.kafka.config.KafkaConfig;
import com.nocmok.orp.kafka.orp_output.AssignRequestNotification;
import com.nocmok.orp.kafka.orp_telemetry.VehicleTelemetryMessage;
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
import org.springframework.kafka.support.converter.StringJsonMessageConverter;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;

@Configuration
@EnableKafka
public class KafkaConsumerConfig {

    @Autowired
    private KafkaConfig kafkaConfig;

    @Value("${kafka.consumers.orp_notifier.group_id}")
    private String kafkaOrpNotifierConsumerGroupId;

    @Value("${kafka.consumers.orp_notifier.n_threads}")
    private Integer nThreads;

    @Bean("orpTelemetryConsumerFactory")
    public ConsumerFactory<String, VehicleTelemetryMessage> orpTelemetryConsumerFactory() {
        var props = new HashMap<String, Object>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.getKafkaBootstrapAddress());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaOrpNotifierConsumerGroupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        return new DefaultKafkaConsumerFactory<>(props,
                new ErrorHandlingDeserializer<>(new StringDeserializer()),
                new ErrorHandlingDeserializer<>(new JsonDeserializer<>(VehicleTelemetryMessage.class)));
    }

    @Bean("orpTelemetryKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, VehicleTelemetryMessage> orpTelemetryKafkaListenerContainerFactory() {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, VehicleTelemetryMessage>();
        factory.setConcurrency(nThreads);
        factory.setConsumerFactory(orpTelemetryConsumerFactory());
//        factory.setErrorHandler();
        return factory;
    }

    @Bean("orpOutputConsumerFactory")
    public ConsumerFactory<String, AssignRequestNotification> orpOutputConsumerFactory() {
        var props = new HashMap<String, Object>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.getKafkaBootstrapAddress());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaOrpNotifierConsumerGroupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        return new DefaultKafkaConsumerFactory<>(props,
                new ErrorHandlingDeserializer<>(new StringDeserializer()),
                new ErrorHandlingDeserializer<>(new JsonDeserializer<>(AssignRequestNotification.class)));
    }

    @Bean("orpOutputKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, AssignRequestNotification> orpOutputKafkaListenerContainerFactory() {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, AssignRequestNotification>();
        factory.setConcurrency(nThreads);
        factory.setConsumerFactory(orpOutputConsumerFactory());
//        factory.setErrorHandler();
        return factory;
    }
}
