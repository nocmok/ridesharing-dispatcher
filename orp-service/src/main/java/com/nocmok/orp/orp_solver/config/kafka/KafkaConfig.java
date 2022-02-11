package com.nocmok.orp.orp_solver.config.kafka;

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
        KafkaProducerConfig.class,
        KafkaConsumerConfig.class
})
@Getter
public class KafkaConfig {

    @Value("${kafka.bootstrapAddress}")
    private String kafkaBootstrapAddress;

    @Value("${kafka.topics.orp_input.partitions}")
    private Integer kafkaOrpInputTopicPartitions;

    @Value("${kafka.topics.orp_input.replicas}")
    private Short kafkaOrpInputTopicReplicas;

    @Value("${kafka.topics.orp_output.partitions}")
    private Integer kafkaOrpOutputTopicPartitions;

    @Value("${kafka.topics.orp_output.replicas}")
    private Short kafkaOrpOutputTopicReplicas;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        var props = new HashMap<String, Object>();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapAddress);
        return new KafkaAdmin(props);
    }

    @Bean
    public NewTopic orpInputTopic() {
        return new NewTopic("orp.input", kafkaOrpInputTopicPartitions, kafkaOrpInputTopicReplicas);
    }

    @Bean
    public NewTopic orpOutputTopic() {
        return new NewTopic("orp.input", kafkaOrpOutputTopicPartitions, kafkaOrpOutputTopicReplicas);
    }
}
