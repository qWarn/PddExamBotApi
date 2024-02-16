package ru.qwarn.pddexambotapi.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import java.util.Random;

@Configuration
@EnableAspectJAutoProxy
public class AppConfig {

    @Bean
    public Random random(){
        return new Random();
    }
}
