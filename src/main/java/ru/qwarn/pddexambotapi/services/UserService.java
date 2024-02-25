package ru.qwarn.pddexambotapi.services;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.qwarn.pddexambotapi.exceptions.ListIsEmptyException;
import ru.qwarn.pddexambotapi.exceptions.UserDoesntExistsException;
import ru.qwarn.pddexambotapi.models.User;
import ru.qwarn.pddexambotapi.repositories.UserRepository;
import ru.qwarn.pddexambotapi.utils.MessageCreator;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;


    public User getByChatId(long chatId) {
        return userRepository.findById(chatId).orElseThrow(UserDoesntExistsException::new);
    }

    @Transactional
    public ResponseEntity<PartialBotApiMethod<Message>> getFinishMessage(long chatId) {
        User user = getByChatId(chatId);
        user.setQuestion(null);

        return ResponseEntity.ok()
                .header("MessageType", "sendmessage")
                .body(MessageCreator.createFinishMessage(
                        chatId, user.getFailsCount()
                ));
    }

    @Transactional
    public ResponseEntity<SendMessage> getGreetingMessage(long chatId) {
        Optional<User> optionalUser = userRepository.findById(chatId);
        if (optionalUser.isEmpty()) {
            userRepository.save(new User(chatId));
        }
        return new ResponseEntity<>(MessageCreator.createGreetingMessage(chatId), HttpStatus.OK);
    }

    @Transactional
    public List<SendMessage> getUsersWithLastActiveMoreThanTwoDays() {
        List<SendMessage> messages = userRepository.findAll().stream()
                .filter(user -> user.getLastActive() != null && user.getLastActive().toInstant()
                        .isBefore(Instant.now().minus(2, ChronoUnit.DAYS)))
                .map(user -> {
                    user.setLastActive(null);
                    return MessageCreator.createNotificationMessage(user.getChatId());
                })
                .toList();

        if (messages.isEmpty()) {
            throw new ListIsEmptyException("No users with last_active more than 2 days found");
        }

        return messages;
    }


}
