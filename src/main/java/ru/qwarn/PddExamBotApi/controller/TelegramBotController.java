package ru.qwarn.PddExamBotApi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.qwarn.PddExamBotApi.botutils.MessageChecker;
import ru.qwarn.PddExamBotApi.botutils.MessageSender;
import ru.qwarn.PddExamBotApi.exceptions.InvalidAnswerException;
import ru.qwarn.PddExamBotApi.exceptions.QuestionNotFoundException;
import ru.qwarn.PddExamBotApi.exceptions.SelectedQuestionsIsEmptyException;
import ru.qwarn.PddExamBotApi.models.*;
import ru.qwarn.PddExamBotApi.services.QuestionService;
import ru.qwarn.PddExamBotApi.services.SelectedQuestionsService;
import ru.qwarn.PddExamBotApi.services.TicketService;
import ru.qwarn.PddExamBotApi.services.UserService;

import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/bot/api")
public class TelegramBotController {

    private final UserService userService;

    private final MessageChecker messageChecker;

    private final TicketService ticketService;

    private final QuestionService questionService;

    private final SelectedQuestionsService selectedQuestionsService;

    private final MessageSender messageSender;
    @Autowired
    public TelegramBotController(UserService userService, MessageChecker messageChecker, TicketService ticketService, QuestionService questionService, SelectedQuestionsService selectedQuestionsService, MessageSender messageSender) {
        this.userService = userService;
        this.messageChecker = messageChecker;
        this.ticketService = ticketService;
        this.questionService = questionService;
        this.selectedQuestionsService = selectedQuestionsService;
        this.messageSender = messageSender;
    }

    @PostMapping("/start/{chatId}")
    public ResponseEntity<SendMessage> startBot(@PathVariable long chatId){
        User user = userService.findByChatId(chatId);

        if (user == null){
            user = new User(chatId);
            userService.save(user);
        }

        return new ResponseEntity<>(messageSender.sendThickets(user, 1, 20), HttpStatus.OK);
    }

    @PatchMapping("/tickets/{chatId}")
    public ResponseEntity<SendMessage> showTickets(@PathVariable long chatId, @RequestParam(name = "next", required = false) boolean showNextTickets,
                                                   @RequestParam(name = "prev", required = false) boolean showPrevTickets){
        SendMessage ticketMessage = showPrevTickets ?
                messageSender.sendThickets(userService.findByChatId(chatId), 1, 20 ) :
                showNextTickets ?  messageSender.sendThickets(userService.findByChatId(chatId), 20, 40) :
                        messageSender.sendThickets(userService.findByChatId(chatId), 1, 20);

        return new ResponseEntity<>(ticketMessage, HttpStatus.OK);
    }

    @GetMapping("/getAnswer/{chatId}")
    public ResponseEntity<SendMessage> getAnswerResult(@PathVariable long chatId, @RequestParam(name = "answer") int answerNumber){
        User user = userService.findByChatId(chatId);
        Question currentQuestion = user.getQuestion();
        if (currentQuestion == null) {
            throw new QuestionNotFoundException("Пользователь не выбрал вопрос!");
        }
        return new ResponseEntity<>(messageSender.sendUserAnswerAndGetCorrect(chatId, answerNumber, currentQuestion, user), HttpStatus.OK);

    }

    @PatchMapping("/nextQuestion/{chatId}")
    public ResponseEntity<?> nextQuestion(@PathVariable long chatId){
        User user = userService.findByChatId(chatId);
        Question currentQuestion = user.getQuestion();
        if (user.getTicket() != null){
            if (currentQuestion.getOrderInThicket() == 20) {
                return new ResponseEntity<>(messageSender.sendFinishMessage(chatId, user), HttpStatus.OK);
            }

            return saveThanCheckIfQuestionHasImageAndReturnQuestion(chatId,
                    questionService.findById(currentQuestion.getId() + 1),
                    user);
        }else {
            List<SelectedQuestions> selectedQuestions = selectedQuestionsService.findAllByUserAndAlreadyWasFalse(user);
            if (selectedQuestions.size() == 0){
                return new ResponseEntity<>(messageSender.sendFinishMessage(chatId, user), HttpStatus.OK);
            }

            SelectedQuestions selectedQuestion = selectedQuestions.get(
                    selectedQuestions.size() == 1 ? 0 : new Random().nextInt(selectedQuestions.size()-1));
            selectedQuestion.setAlreadyWas(true);
            selectedQuestionsService.save(selectedQuestion);
            Question nextQuestion = selectedQuestion.getQuestion();

            return saveThanCheckIfQuestionHasImageAndReturnQuestion(chatId, nextQuestion, user);
        }
    }

    @PatchMapping("/ticket/{chatId}/{ticketId}")
    public ResponseEntity<?> ticket(@PathVariable long chatId, @PathVariable int ticketId){
        User user = userService.findByChatId(chatId);
        Ticket ticket = ticketService.findById(ticketId);

        user.setTicket(ticket);
        ticket.getUsers().add(user);
        Question question = questionService.findByThicketAndId(ticket.getId(), 1);

        return saveThanCheckIfQuestionHasImageAndReturnQuestion(chatId, question, user);
    }

    @PatchMapping("/selectedQuestions/{chatId}")
    public ResponseEntity<?> selectedQuestions(@PathVariable long chatId){
        User user = userService.findByChatId(chatId);
        List<SelectedQuestions> selectedQuestions = selectedQuestionsService.findAllByUser(user);
        if (selectedQuestions.isEmpty()){
            throw new SelectedQuestionsIsEmptyException("Список избранных пуст! Вам следует добавить хоть 1 вопрос в избранное!");
        }

        return new ResponseEntity<>(saveThanCheckIfQuestionHasImageAndReturnQuestion(chatId,
                getNextQuestionFromSelected(selectedQuestions),
                user), HttpStatus.OK);
    }

    @ExceptionHandler
    public ResponseEntity<String> handleQuestionNotFoundException(QuestionNotFoundException e){
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<String> handleInvalidAnswer(InvalidAnswerException e){
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<String> handleSelectedQuestionIsEmpty(SelectedQuestionsIsEmptyException e){
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    //TODO переместить в специальный класс!
    public ResponseEntity<?> saveThanCheckIfQuestionHasImageAndReturnQuestion(long chatId, Question question, User user){
        user.setQuestion(question);
        userService.save(user);

        if (question.getImageURI() != null){
            return new ResponseEntity<>(messageSender.sendQuestionWithoutImage(chatId, question), HttpStatus.OK);
        }else {
            return new ResponseEntity<>(messageSender.sendQuestionWithImage(chatId, question), HttpStatus.OK);
        }
    }

    public Question getNextQuestionFromSelected(List<SelectedQuestions> selectedQuestions){
        SelectedQuestions selectedQuestion = selectedQuestions.get(
                selectedQuestions.size() == 1 ? 0 : new Random().nextInt(selectedQuestions.size()-1));
        selectedQuestion.setAlreadyWas(true);
        selectedQuestionsService.save(selectedQuestion);
        return selectedQuestion.getQuestion();
    }


}
