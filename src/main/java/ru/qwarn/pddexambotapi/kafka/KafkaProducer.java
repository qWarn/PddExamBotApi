package ru.qwarn.pddexambotapi.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.qwarn.pddexambotapi.services.UserService;
import ru.qwarn.pddexambotapi.utils.MessageCreator;


@Component
@RequiredArgsConstructor
public class KafkaProducer {

    private final KafkaTemplate<String, SendMessage> template;
    private final UserService userService;

    @Value("${kafka.topics.notificationTopic}")
    private String notificationTopicName;

    @Scheduled(fixedRate = 60000)
    public void checkInactiveUsersAndSendMessage() {
        userService.getUsersWithLastActiveMoreThanTwoDays()
                .forEach(u -> {
                    userService.save(u);
                    template.send(notificationTopicName, MessageCreator.createNotificationMessage(u));
                });
    }

}
