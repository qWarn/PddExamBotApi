package ru.qwarn.pddexambotapi.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.qwarn.pddexambotapi.exceptions.InvalidAnswerException;
import ru.qwarn.pddexambotapi.exceptions.QuestionNotFoundException;
import ru.qwarn.pddexambotapi.exceptions.SelectedQuestionsIsEmptyException;
import ru.qwarn.pddexambotapi.exceptions.UserDoesntExistsException;
import ru.qwarn.pddexambotapi.services.QuestionService;
import ru.qwarn.pddexambotapi.services.UserService;

@RestController
@Slf4j
@RequestMapping("/bot/api")
@RequiredArgsConstructor // TODO: использовать одинаковый подход
public class TelegramBotController {

    private final UserService userService;
    private final QuestionService questionService;

    @PostMapping("/start/{chatId}")
    public ResponseEntity<SendMessage> startBot(@PathVariable long chatId) {
        return userService.addUserOrSendTickets(chatId);
    }

    @PatchMapping("/tickets/{chatId}")
    public ResponseEntity<SendMessage> showTickets(@PathVariable long chatId,
                                                   @RequestParam(required = false) boolean next) {
        return userService.createMessageWithTickets(chatId, next);
    }

    @GetMapping("/getAnswer/{chatId}")
    public ResponseEntity<SendMessage> getAnswerResult(@PathVariable long chatId, @RequestParam int answer)  {
        return userService.getAnswerResult(chatId, answer);
    }

    @PatchMapping("/nextQuestion/{chatId}")
    public ResponseEntity<PartialBotApiMethod<Message>> nextQuestion(@PathVariable long chatId)  {
        return questionService.nextQuestion(chatId);
    }

    @PatchMapping("/ticket/{chatId}/{ticketId}")
    public ResponseEntity<PartialBotApiMethod<Message>> ticket(@PathVariable long chatId,
                                                               @PathVariable int ticketId)  {
        return questionService.getFirstQuestionFromTicket(chatId, ticketId);
    }

    @PatchMapping("/selectedQuestions/{chatId}")
    public ResponseEntity<PartialBotApiMethod<Message>> selectedQuestions(@PathVariable long chatId) {
        return questionService.getFirstSelectedQuestion(chatId);
    }

    @PatchMapping("/addToSelected/{chatId}/{questionId}")
    public void addQuestionToSelected(@PathVariable long chatId, @PathVariable int questionId) {
        questionService.addQuestionToSelected(chatId, questionId);
    }

    @PatchMapping("/removeFromSelected/{chatId}/{questionId}")
    public void removeSelectedQuestion(@PathVariable long chatId, @PathVariable int questionId) {
        questionService.removeQuestionFromSelected(chatId, questionId);
    }

    @ExceptionHandler
    public ResponseEntity<String> handleQuestionNotFoundException(QuestionNotFoundException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<String> handleInvalidAnswer(InvalidAnswerException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<String> handleSelectedQuestionIsEmpty(SelectedQuestionsIsEmptyException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<String> handleUserDoesntExistsException(UserDoesntExistsException e) {
        log.info(e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
    }
}
