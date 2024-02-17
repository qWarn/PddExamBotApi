package ru.qwarn.pddexambotapi.services;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.qwarn.pddexambotapi.dto.TaskDTO;
import ru.qwarn.pddexambotapi.exceptions.InvalidAnswerException;
import ru.qwarn.pddexambotapi.exceptions.QuestionNotFoundException;
import ru.qwarn.pddexambotapi.models.Answer;
import ru.qwarn.pddexambotapi.models.Question;
import ru.qwarn.pddexambotapi.models.User;
import ru.qwarn.pddexambotapi.utils.MessageCreator;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TelegramBotService {

    private final UserService userService;
    private final SelectedService selectedService;
    private final QuestionService questionService;
    private final AnswerService answerService;

    @Transactional
    public ResponseEntity<PartialBotApiMethod<Message>> generateQuestion(long chatId, TaskDTO taskDTO) {
        User user = userService.getByChatId(chatId);
        defineTaskType(taskDTO, user);

        Question currentQuestion = user.getQuestion();

        if (user.getTaskType().equals("ticket")) {
            if (currentQuestion.getOrderInTicket() == 20) {
                return userService.endMessage(chatId);
            }
            return questionResponse(chatId, currentQuestion);
        } else {
            Optional<Question> questionOptional = selectedService.generateQuestionFromSelected(user);
            if (questionOptional.isEmpty()) {
                return userService.endMessage(chatId);
            }
            user.setQuestion(questionOptional.get());

            return questionResponse(chatId, questionOptional.get());
        }
    }

    private void defineTaskType(TaskDTO taskDTO, User user) {
        if (taskDTO != null && taskDTO.type().equals("selected")) {
            user.setFailsCount(0);
            user.setTaskType(taskDTO.type());
            selectedService.refreshSelectedQuestions(user);
        } else if (taskDTO != null && taskDTO.type().equals("ticket")) {
            user.setFailsCount(0);
            user.setTaskType(taskDTO.type());
            user.setQuestion(questionService.getFirstQuestion(taskDTO.ticketId()));
        }
    }


    private ResponseEntity<PartialBotApiMethod<Message>> questionResponse(long chatId, Question question) {
        List<Answer> answers = answerService.getAnswers(question.getId());

        if (question.getImageURI() == null) {
            return ResponseEntity.ok().header("MessageType", "sendmessage")
                    .body(MessageCreator.createQuestionWithoutMessage(chatId, question, answers));
        } else {
            return ResponseEntity.ok().header("MessageType", "sendphoto")
                    .body(MessageCreator.createQuestionWithImage(chatId, question, answers));
        }
    }

    @Transactional
    public ResponseEntity<SendMessage> generateGreetingsOrTickets(long chatId) {
        return userService.sendGreetingOrTickets(chatId);
    }

    @Transactional
    public ResponseEntity<SendMessage> generateTickets(long chatId, boolean next) {
        User user = userService.getByChatId(chatId);
        user.setTaskType("none");
        user.setQuestion(null);
        user.setFailsCount(0);

        return ResponseEntity.ok(MessageCreator.createTicketsMessage(user, next));
    }

    @Transactional
    public ResponseEntity<SendMessage> generateAnswerResult(long chatId, int answerNumber) {
        User user = userService.getByChatId(chatId);
        Question currQuestion = user.getQuestion();
        if (currQuestion == null) {
            throw new QuestionNotFoundException("Вы не выбрали билет!");
        }
        if (answerNumber > answerService.getAnswers(currQuestion.getId()).size() || answerNumber <= 0) {
            throw new InvalidAnswerException("Ответ под номером " + answerNumber + " не существует!");
        }

        boolean isCorrect = currQuestion.getCorrectAnswerNumber() == answerNumber;
        user.setFailsCount(user.getFailsCount() + (isCorrect ? 0 : 1));
        user.setQuestion(questionService.getById(currQuestion.getId() + 1));
        user.setLastActive(Timestamp.from(Instant.now()));
        return ResponseEntity.ok(MessageCreator
                .createAnswer(chatId, currQuestion, selectedService.getByQuestionAndUser(currQuestion, user), isCorrect));
    }

    @Transactional
    public void addQuestionToSelected(long chatId, int questionId) {
        selectedService.save(userService.getByChatId(chatId), questionService.getById(questionId));
    }

    @Transactional
    public void removeQuestionFromSelected(long chatId, int questionId) {
        selectedService.removeFromSelected(questionService.getById(questionId), userService.getByChatId(chatId));
    }

}
