package ru.qwarn.pddexambotapi.services;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.qwarn.pddexambotapi.exceptions.InvalidAnswerException;
import ru.qwarn.pddexambotapi.exceptions.QuestionNotFoundException;
import ru.qwarn.pddexambotapi.exceptions.UserDoesntExistsException;
import ru.qwarn.pddexambotapi.models.Question;
import ru.qwarn.pddexambotapi.models.User;
import ru.qwarn.pddexambotapi.repositories.UserRepository;
import ru.qwarn.pddexambotapi.utils.MessageCreator;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final MessageCreator messageCreator;
    private final AnswerService answerService;
    private final SelectedQuestionsService selectedQuestionsService;

    @Transactional
    public void save(User user) {
        user.setLastActive(Timestamp.from(Instant.now()));
        userRepository.save(user);
    }

    public Optional<User> findByChatId(long chatId) {
        return userRepository.findByChatId(chatId);
    }

    @Transactional
    public ResponseEntity<SendMessage> addUserOrSendTickets(long chatId) {
        Optional<User> user = findByChatId(chatId);

        if (user.isEmpty()) {
            save(new User(chatId));
            return new ResponseEntity<>(messageCreator.createMessageForNewUser(chatId), HttpStatus.OK);
        }

        return ResponseEntity.ok(MessageCreator.createTicketsMessage(user.get(), 1, 20));
    }

    @Transactional
    public ResponseEntity<SendMessage> createMessageWithTickets(long chatId, boolean next) {
        User user = findByChatId(chatId).orElseThrow(UserDoesntExistsException::new);
        user.setTaskType("none");
        user.setQuestion(null);
        user.setFailsCount(0);

        save(user);

        int from = next ? 21 : 1;
        return ResponseEntity.ok(MessageCreator.createTicketsMessage(user, from, from + 20));
    }

    @Transactional
    public ResponseEntity<SendMessage> getAnswerResult(long chatId, int answerNumber) {
        User user = findByChatId(chatId).orElseThrow(UserDoesntExistsException::new);
        Question currQuestion = user.getQuestion();

        if (currQuestion == null) {
            throw new QuestionNotFoundException("Вы не выбрали билет!");
        }

        if (answerNumber > answerService.getQuestionAnswers(currQuestion.getId()).size() || answerNumber <= 0) {
            throw new InvalidAnswerException("Ответ под номером " + answerNumber + " не существует!");
        }

        boolean isCorrect = currQuestion.getCorrectAnswerNumber() == answerNumber;

        user.setFailsCount(user.getFailsCount() + (isCorrect ? 0 : 1));


        return ResponseEntity.ok(MessageCreator.createAnswer(chatId, currQuestion,
                selectedQuestionsService.findByQuestionAndUser(currQuestion, user),
                isCorrect));
    }

    @Transactional
    public ResponseEntity<PartialBotApiMethod<Message>> getFinishMessage(long chatId) {
        User user = findByChatId(chatId).orElseThrow(UserDoesntExistsException::new);

        user.setQuestion(null);
        int fails = user.getFailsCount();

        return ResponseEntity.ok(MessageCreator.createFinishMessage(chatId, fails));
    }

    public List<User> getUsersWithLastActiveMoreThanTwoDays() {
        return userRepository.findAll().stream()
                .filter(user -> user.getLastActive().toInstant()
                        .isBefore(Instant.now().minus(2, ChronoUnit.DAYS)))
                .toList();
    }
}
