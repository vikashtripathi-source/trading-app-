package com.trad.tech.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Value("${kafka.topics.trade-events}")
    private String tradeEventsTopic;

    @Value("${kafka.topics.trade-validation}")
    private String tradeValidationTopic;

    @Value("${kafka.topics.trade-execution}")
    private String tradeExecutionTopic;

    @Value("${kafka.topics.trade-errors}")
    private String tradeErrorsTopic;

    @Value("${kafka.topics.market-data}")
    private String marketDataTopic;

    @Bean
    public NewTopic tradeEventsTopic() {
        return TopicBuilder.name(tradeEventsTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic tradeValidationTopic() {
        return TopicBuilder.name(tradeValidationTopic)
                .partitions(2)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic tradeExecutionTopic() {
        return TopicBuilder.name(tradeExecutionTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic tradeErrorsTopic() {
        return TopicBuilder.name(tradeErrorsTopic)
                .partitions(2)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic marketDataTopic() {
        return TopicBuilder.name(marketDataTopic)
                .partitions(5)
                .replicas(1)
                .build();
    }
}
