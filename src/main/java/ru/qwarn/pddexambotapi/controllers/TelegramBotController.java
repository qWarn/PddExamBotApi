package ru.qwarn.pddexambotapi.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.qwarn.pddexambotapi.dto.TaskDTO;
import ru.qwarn.pddexambotapi.services.TelegramBotService;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TelegramBotController {

    private final TelegramBotService telegramBotService;

    @PostMapping("/start/{chatId}")
    public ResponseEntity<SendMessage> startBot(@PathVariable long chatId) {
        return telegramBotService.getGreetingMessageResponse(chatId);
    }

    @PatchMapping("/tickets/{chatId}")
    public ResponseEntity<SendMessage> getTickets(@PathVariable long chatId, @RequestParam(required = false) boolean next) {
        return telegramBotService.getTicketsResponse(chatId, next);
    }

    @PatchMapping("/getAnswer/{chatId}")
    public void checkAnswer(@PathVariable long chatId, @RequestParam int answer) {
        telegramBotService.checkAnswer(chatId, answer);
    }

    @PatchMapping("/nextQuestion/{chatId}")
    public ResponseEntity<PartialBotApiMethod<Message>> getQuestion(@PathVariable long chatId) {
        return telegramBotService.getQuestionResponse(chatId);
    }

    @PatchMapping("/addToSelected/{chatId}/{questionId}")
    public void addToSelected(@PathVariable long chatId, @PathVariable int questionId) {
        telegramBotService.addQuestionToSelected(chatId, questionId);
    }

    @PatchMapping("/removeFromSelected/{chatId}/{questionId}")
    public void removeFromSelected(@PathVariable long chatId, @PathVariable int questionId) {
        telegramBotService.removeQuestionFromSelected(chatId, questionId);
    }
    @PatchMapping("/{chatId}/set/task")
    public ResponseEntity<SendMessage> startTask(@PathVariable long chatId, @RequestBody TaskDTO taskDTO){
        return telegramBotService.getStartTaskResponse(chatId, taskDTO);
    }

    @GetMapping("/correctAnswer/{chatId}")
    public ResponseEntity<SendMessage> getCorrectAnswer(@PathVariable long chatId){
        return telegramBotService.getCorrectAnswerResponse(chatId);
    }

}
