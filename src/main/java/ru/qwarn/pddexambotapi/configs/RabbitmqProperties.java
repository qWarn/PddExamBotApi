package ru.qwarn.pddexambotapi.configs;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "rabbitmq")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
public class RabbitmqProperties {

    String routingKey;
    String queue;
    String exchange;

}
