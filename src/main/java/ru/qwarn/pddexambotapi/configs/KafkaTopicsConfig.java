package ru.qwarn.pddexambotapi.configs;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicsConfig {

    @Value("${kafka.topics.notificationTopic}")
    private String notificationTopicName;

    @Bean
    public NewTopic topics() {
        return TopicBuilder.name(notificationTopicName).build();
    }
}
