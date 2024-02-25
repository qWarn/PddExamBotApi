package ru.qwarn.pddexambotapi.services;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.qwarn.pddexambotapi.dto.TaskDTO;
import ru.qwarn.pddexambotapi.models.*;
import ru.qwarn.pddexambotapi.utils.MessageCreator;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static ru.qwarn.pddexambotapi.models.TaskType.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TelegramBotService {

    private final UserService userService;
    private final SelectedService selectedService;
    private final QuestionService questionService;
    private final AnswerService answerService;

    @Transactional
    public ResponseEntity<PartialBotApiMethod<Message>> getQuestionResponse(long chatId) {
        User user = userService.getByChatId(chatId);
        Question currentQuestion = user.getQuestion();
        if (user.getTaskType().equals(TICKET)) {
            if (currentQuestion == null) {
                return userService.getFinishMessage(chatId);
            }
            return initQuestion(chatId, currentQuestion);
        } else {
            Optional<Question> questionOptional = selectedService.getQuestionFromSelected(user);
            if (questionOptional.isEmpty()) {
                return userService.getFinishMessage(chatId);
            }
            user.setQuestion(questionOptional.get());

            return initQuestion(chatId, questionOptional.get());
        }
    }

    @Transactional
    public ResponseEntity<SendMessage> getStartTaskResponse(long chatId, TaskDTO taskDTO) {
        User user = userService.getByChatId(chatId);
        user.setFailsCount(0);
        user.setTaskType(taskDTO.taskType());
        if (taskDTO.taskType().equals(TICKET)) {
            user.setQuestion(questionService.getFirstQuestion(taskDTO.ticketId()));
        }
        if (taskDTO.taskType().equals(SELECTED)) {
            selectedService.refreshSelectedQuestions(user);
        }

        return ResponseEntity.ok(MessageCreator.createStartMessage(chatId, taskDTO));
    }

    public ResponseEntity<SendMessage> getCorrectAnswerResponse(long chatId){
        User user = userService.getByChatId(chatId);
        Question question = user.getQuestion();
        return ResponseEntity.ok(MessageCreator.createAnswer(chatId, question));
    }


    private ResponseEntity<PartialBotApiMethod<Message>> initQuestion(long chatId, Question question) {
        List<Answer> answers = answerService.getAnswers(question);
        boolean isSelected = selectedService.getById(chatId, question.getId()).isPresent();
        if (question.getImageURI() == null) {
            return ResponseEntity.ok().header("MessageType", "sendmessage")
                    .body(MessageCreator.createQuestionWithoutMessage(chatId, question, answers, isSelected));
        } else {
            return ResponseEntity.ok().header("MessageType", "sendphoto")
                    .body(MessageCreator.createQuestionWithImage(chatId, question, answers, isSelected));
        }
    }

    @Transactional
    public ResponseEntity<SendMessage> getGreetingMessageResponse(long chatId) {
        return userService.getGreetingMessage(chatId);
    }

    @Transactional
    public ResponseEntity<SendMessage> getTicketsResponse(long chatId, boolean next) {
        User user = userService.getByChatId(chatId);
        user.setTaskType(NONE);
        user.setQuestion(null);
        user.setFailsCount(0);

        return ResponseEntity.ok(MessageCreator.createTicketsMessage(chatId, next));
    }

    @Transactional
    public void checkAnswer(long chatId, int answerNumber) {
        User user = userService.getByChatId(chatId);
        Question currQuestion = user.getQuestion();

        if (user.getTaskType().equals(SELECTED)){
            selectedService.getById(chatId, currQuestion.getId()).ifPresent(question -> question.setAlreadyWas(true));
        }else {
            if (currQuestion.getOrderInTicket() == 20){
                user.setQuestion(null);
            }else {
                user.setQuestion(questionService.getById(currQuestion.getId() + 1));
            }
        }
        boolean isCorrect = currQuestion.getCorrectAnswerNumber() == answerNumber;
        user.setFailsCount(user.getFailsCount() + (isCorrect ? 0 : 1));
        user.setLastActive(Timestamp.from(Instant.now()));
    }

    @Transactional
    public void addQuestionToSelected(long chatId, int questionId) {
        selectedService.save(userService.getByChatId(chatId), questionService.getById(questionId));
    }

    @Transactional
    public void removeQuestionFromSelected(long chatId, int questionId) {
        selectedService.removeFromSelected(userService.getByChatId(chatId), questionService.getById(questionId));
    }

}
