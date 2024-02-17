package ru.qwarn.pddexambotapi;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.qwarn.pddexambotapi.controllers.TelegramBotController;

import static org.springframework.util.Assert.*;

@SpringBootTest
class PddExamBotApiApplicationTests {
	@Autowired
	private TelegramBotController telegramBotController;

	@Test
	void contextLoads() {
		notNull(telegramBotController, "Context loads failed: didn't load TelegramBotController");
	}

}
