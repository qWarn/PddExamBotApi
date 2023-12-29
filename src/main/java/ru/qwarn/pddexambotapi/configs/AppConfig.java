package ru.qwarn.pddexambotapi.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Random;

@Configuration
@EnableScheduling
public class AppConfig {

    @Bean
    public Random random(){
        return new Random();
    }
}
