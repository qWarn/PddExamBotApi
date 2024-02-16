package ru.qwarn.pddexambotapi.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.qwarn.pddexambotapi.dto.TaskDTO;
import ru.qwarn.pddexambotapi.exceptions.InvalidAnswerException;
import ru.qwarn.pddexambotapi.exceptions.QuestionNotFoundException;
import ru.qwarn.pddexambotapi.exceptions.UserDoesntExistsException;
import ru.qwarn.pddexambotapi.services.TelegramBotService;

@RestController
@Slf4j
@RequestMapping("/bot/api")
@RequiredArgsConstructor
public class TelegramBotController {

    private final TelegramBotService telegramBotService;

    @Operation(summary = "Register user in db and send start message or send message with tickets if user already exists")
    @ApiResponse(responseCode = "200", description = "Registered user or sent message with tickets",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = SendMessage.class))})
    @PostMapping("/start/{chatId}")
    public ResponseEntity<SendMessage> startBot(
            @Parameter(name = "chatId", description = "user's chatId") @PathVariable long chatId) {
        return telegramBotService.generateGreetingsOrTickets(chatId);
    }


    @Operation(summary = "Send user message with tickets")
    @ApiResponse(responseCode = "200", description = "Sent message with tickets", content =
            {@Content(mediaType = "application/json", schema = @Schema(implementation = SendMessage.class))})
    @PatchMapping("/tickets/{chatId}")
    public ResponseEntity<SendMessage> showTickets(
            @Parameter(name = "chatId", description = "user's chatId") @PathVariable long chatId,
            @Parameter(name = "next", description = "Flag. If it's true returns message with tickets from 21 to 40 otherwise from 1 to 20")
            @RequestParam(required = false) boolean next) {
        return telegramBotService.generateTickets(chatId, next);
    }

    @Operation(summary = "Get answer result")
    @ApiResponse(responseCode = "200", description = "Sent message with answer result and explanation",
            content =
            @Content(mediaType = "application/json", schema = @Schema(implementation = SendMessage.class)))
    @ApiResponse(responseCode = "404",
            description = "If user tried to get answer but he isn't currently doing any ticket",
            content =
            @Content(mediaType = "application/json", schema = @Schema(implementation = QuestionNotFoundException.class)))
    @ApiResponse(responseCode = "400", description = "If user chose illegal answer number",
            content =
            @Content(mediaType = "application/json", schema = @Schema(implementation = InvalidAnswerException.class)))
    @PatchMapping("/getAnswer/{chatId}")
    public ResponseEntity<SendMessage> getAnswerResult(
            @Parameter(name = "chatId", description = "user's chatId") @PathVariable long chatId,
            @Parameter(name = "answer", description = "Number, that user has chosen") @RequestParam int answer) {
        return telegramBotService.generateAnswerResult(chatId, answer);
    }

    @Operation(summary = "Returns next question from ticket of from user's selected list")
    @ApiResponse(responseCode = "200",
            description = "Returned next question (SendPhoto if question has image or SendMessage otherwise).",
            content =
            @Content(mediaType = "application/json", schema = @Schema(oneOf = {SendPhoto.class, SendMessage.class})))
    @ApiResponse(responseCode = "401", description =
            "Returned exception with message which is asking user to type command /start",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDoesntExistsException.class)))
    @PatchMapping("/nextQuestion/{chatId}")
    public ResponseEntity<PartialBotApiMethod<Message>> nextQuestion(
            @RequestBody(required = false) TaskDTO taskDTO,
            @Parameter(name = "chatId", description = "user's chatId") @PathVariable long chatId) {
        return telegramBotService.generateQuestion(chatId, taskDTO);
    }


    @Operation(summary = "Adds question to selected list")
    @ApiResponse(responseCode = "200", description = "Added question to selected list. Returned nothing")
    @ApiResponse(responseCode = "401", description =
            "Returned exception with message which is asking user to type command /start",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDoesntExistsException.class)))
    @PatchMapping("/addToSelected/{chatId}/{questionId}")
    public void addQuestionToSelected(
            @Parameter(name = "chatId", description = "user's chatId") @PathVariable long chatId,
            @Parameter(name = "questionId", description = "current question's id") @PathVariable int questionId) {
        telegramBotService.addQuestionToSelected(chatId, questionId);
    }

    @Operation(summary = "Removes question from selected list")
    @ApiResponse(responseCode = "200", description = "Removed question to selected list. Returned nothing")
    @ApiResponse(responseCode = "401", description =
            "Returned exception with message which is asking user to type command /start",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDoesntExistsException.class)))
    @PatchMapping("/removeFromSelected/{chatId}/{questionId}")
    public void removeSelectedQuestion(
            @Parameter(name = "chatId", description = "user's chatId") @PathVariable long chatId,
            @Parameter(name = "questionId", description = "current question's id") @PathVariable int questionId) {
        telegramBotService.removeQuestionFromSelected(chatId, questionId);
    }

}
