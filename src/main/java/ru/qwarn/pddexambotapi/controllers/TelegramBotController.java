package ru.qwarn.pddexambotapi.controllers;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.qwarn.pddexambotapi.utils.MessageCreator;
import ru.qwarn.pddexambotapi.exceptions.InvalidAnswerException;
import ru.qwarn.pddexambotapi.exceptions.QuestionNotFoundException;
import ru.qwarn.pddexambotapi.exceptions.SelectedQuestionsIsEmptyException;
import ru.qwarn.pddexambotapi.models.*;
import ru.qwarn.pddexambotapi.services.QuestionService;
import ru.qwarn.pddexambotapi.services.SelectedQuestionsService;
import ru.qwarn.pddexambotapi.services.UserService;

import java.rmi.UnexpectedException;
import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/bot/api")
@RequiredArgsConstructor
public class TelegramBotController {

    private final UserService userService;
    private final QuestionService questionService;
    private final SelectedQuestionsService selectedQuestionsService;
    private final MessageCreator messageCreator;

    private final Random random;

    private final Logger logger;


    @PostMapping("/start/{chatId}")
    public ResponseEntity<SendMessage> startBot(@PathVariable long chatId){
        User user = userService.findByChatId(chatId);

        if (user == null){
            userService.save(new User(chatId));
            return new ResponseEntity<>(messageCreator.createMessageForNewUser(chatId), HttpStatus.OK);
        }

        return new ResponseEntity<>(messageCreator.createTicketsMessage(user, 1, 20), HttpStatus.OK);
    }

    @PatchMapping("/tickets/{chatId}")
    public ResponseEntity<SendMessage> showTickets(@PathVariable long chatId, @RequestParam(name = "next", required = false) boolean showNextTickets){
        SendMessage ticketMessage = showNextTickets ?
                messageCreator.createTicketsMessage(userService.findByChatId(chatId), 21, 40 ) :
                  messageCreator.createTicketsMessage(userService.findByChatId(chatId), 1, 20);

        return new ResponseEntity<>(ticketMessage, HttpStatus.OK);
    }

    @GetMapping("/getAnswer/{chatId}")
    public ResponseEntity<SendMessage> getAnswerResult(@PathVariable long chatId, @RequestParam(name = "answer") int answerNumber) throws UnexpectedException {
        User user = userService.findByChatId(chatId);
        Question currentQuestion = user.getQuestion();
        if (currentQuestion == null) {
            throw new QuestionNotFoundException("Вы не выбрали билет!");
        }
        return new ResponseEntity<>(messageCreator.createAnswer(chatId, answerNumber, currentQuestion, user), HttpStatus.OK);
    }

    @PatchMapping("/nextQuestion/{chatId}")
    public ResponseEntity<PartialBotApiMethod<Message>> nextQuestion(@PathVariable long chatId) throws UnexpectedException {
        User user = userService.findByChatId(chatId);
        Question currentQuestion = user.getQuestion();
        if (user.getTaskType().equals("ticket")){
            if (currentQuestion.getOrderInTicket() == 20) {
                return new ResponseEntity<>(messageCreator.createFinishMessage(chatId, user), HttpStatus.OK);
            }

            return questionService.getQuestionResponse(chatId,
                    questionService.findById(currentQuestion.getId() + 1),
                    user);
        }else {
            List<SelectedQuestions> selectedQuestions = selectedQuestionsService.findAllByUserAndAlreadyWasFalse(user);
            if (selectedQuestions.isEmpty()){
                return new ResponseEntity<>(messageCreator.createFinishMessage(chatId, user), HttpStatus.OK);
            }

            SelectedQuestions selectedQuestion = selectedQuestions.get(
                    selectedQuestions.size() == 1 ? 0 : random.nextInt(selectedQuestions.size()-1));
            selectedQuestion.setAlreadyWas(true);
            selectedQuestionsService.save(selectedQuestion);
            Question nextQuestion = selectedQuestion.getQuestion();

            return questionService.getQuestionResponse(chatId, nextQuestion, user);
        }
    }

    @PatchMapping("/ticket/{chatId}/{ticketId}")
    public ResponseEntity<PartialBotApiMethod<Message>> ticket(@PathVariable long chatId, @PathVariable int ticketId) throws UnexpectedException {
        User user = userService.findByChatId(chatId);
        user.setTaskType("ticket");

        Question question = questionService.findByTicketAndId(ticketId, 1);

        return questionService.getQuestionResponse(chatId, question, user);
    }

    @PatchMapping("/selectedQuestions/{chatId}")
    public ResponseEntity<PartialBotApiMethod<Message>> selectedQuestions(@PathVariable long chatId) throws UnexpectedException {
        User user = userService.findByChatId(chatId);
        user.setTaskType("selected");

        List<SelectedQuestions> selectedQuestions = selectedQuestionsService.findAllByUser(user);
        if (selectedQuestions.isEmpty()){
            throw new SelectedQuestionsIsEmptyException("Список избранных пуст! Вам следует добавить хоть 1 вопрос в избранное!");
        }

        selectedQuestions.forEach(question -> {
            question.setAlreadyWas(false);
            selectedQuestionsService.save(question);
        });

        return questionService.getQuestionResponse(chatId,
                selectedQuestionsService.getNextQuestionFromSelected(selectedQuestions),
                user);
    }

    @PatchMapping("/addToSelected/{chatId}/{questionId}")
    public void addQuestionToSelected(@PathVariable long chatId, @PathVariable int questionId) throws UnexpectedException {
        Question question = questionService.findById(questionId);
        User user = userService.findByChatId(chatId);
        if (selectedQuestionsService.findByQuestionAndUser(question, user).isEmpty()) {
            SelectedQuestions selectedQuestion = new SelectedQuestions();
            selectedQuestion.setQuestion(question);
            selectedQuestion.setUser(user);
            selectedQuestionsService.saveQuestionToSelected(selectedQuestion);
        }
    }

    @PatchMapping("/removeFromSelected/{chatId}/{questionId}")
    public void handleRemoveSelectedQuestion(@PathVariable long chatId, @PathVariable int questionId) throws UnexpectedException {
        Question question = questionService.findById(questionId);
        User user = userService.findByChatId(chatId);
        selectedQuestionsService.removeFromSelectedQuestions(question, user);
    }

    @ExceptionHandler
    public ResponseEntity<String> handleQuestionNotFoundException(QuestionNotFoundException e){
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<String> handleInvalidAnswer(InvalidAnswerException e){
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<String> handleSelectedQuestionIsEmpty(SelectedQuestionsIsEmptyException e){
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public void handleUnexpectedException(UnexpectedException e){
        logger.error(e.getMessage());
    }
}
