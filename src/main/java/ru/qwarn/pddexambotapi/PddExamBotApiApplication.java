package ru.qwarn.pddexambotapi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import ru.qwarn.pddexambotapi.controllers.TelegramBotController;

import java.util.Random;

@SpringBootApplication
public class PddExamBotApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(PddExamBotApiApplication.class, args);
	}


}
