package com.nocmok.orp.telemetry.config.kafka;

import lombok.Getter;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;

@Configuration
@Import({
        KafkaConsumerConfig.class
})
@Getter
public class KafkaConfig {

    @Value("${kafka.bootstrapAddress}")
    private String kafkaBootstrapAddress;

    @Value("${kafka.topics.orp_telemetry.partitions}")
    private Integer kafkaOrpInputTopicPartitions;

    @Value("${kafka.topics.orp_telemetry.replicas}")
    private Short kafkaOrpInputTopicReplicas;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        var props = new HashMap<String, Object>();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapAddress);
        return new KafkaAdmin(props);
    }

    @Bean
    public NewTopic orpTelemetryTopic() {
        return new NewTopic("orp.telemetry", kafkaOrpInputTopicPartitions, kafkaOrpInputTopicReplicas);
    }
}
