package ru.qwarn.pddexambotapi.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.qwarn.pddexambotapi.dto.TaskDTO;
import ru.qwarn.pddexambotapi.models.Answer;
import ru.qwarn.pddexambotapi.models.Question;

import java.util.List;
import java.util.stream.Collectors;

import static ru.qwarn.pddexambotapi.models.TaskType.SELECTED;


@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MessageCreator {

    private static final String FINISH_MESSAGE_TEXT = "Вы совершили %d %s.";
    private static final String START_TICKET_TEXT = "Вы выбрали билет под номером %d.";
    private static final String START_SELECTED_TEXT = "Вы выбрали список избранного.";
    private static final String TICKETS_TEXT = "Выберите номер билета для тренировки:";
    private static final String NOTIFICATION_TEXT = "Вы не решали билеты уже 2 дня.";
    private static final String GREETINGS_TEXT = "Привет! \n Этот телеграм бот создан для подготовки к экзамену ПДД. \n Он содержит все 40 актуальных билетов на 2024 год.";

    public static SendMessage createTicketsMessage(long chatId, boolean next) {
        return createMessage(chatId, TICKETS_TEXT, KeyBoardCreator.createTicketsMarkup(next));
    }

    public static SendMessage createAnswer(long chatId, Question question) {
        String text ="Правильный ответ: " + question.getCorrectAnswerNumber() +
                "\n" + question.getCorrectAnswerExplanation();

        return createMessage(chatId, text, KeyBoardCreator.createCorrectAnswerMarkup());
    }

    public static SendMessage createFinishMessage(long chatId, int fails) {
        String text;
        if (fails == 0 || fails > 5) {
            text = String.format(FINISH_MESSAGE_TEXT, fails, "ошибок");
        }else {
            text = String.format(FINISH_MESSAGE_TEXT, fails, fails == 1 ? "ошибку" : "ошибки");
        }
        return createMessage(chatId, text, KeyBoardCreator.createToTicketsMarkup());
    }

    public static SendMessage createQuestionWithoutMessage(long chatId, Question question, List<Answer> answers, boolean isSelected) {
        String text = question.getDescription() + "\n" + answers.stream()
                .map(answer -> answer.getOrderInQuestion() + ". " + answer.getAnswerText() + "\n")
                .collect(Collectors.joining());

        return createMessage(chatId, text, KeyBoardCreator.createAnswersMarkup(answers, question, isSelected));
    }

    public static SendMessage createStartMessage(long chatId, TaskDTO task){
        String text = task.taskType().equals(SELECTED) ? START_SELECTED_TEXT : String.format(START_TICKET_TEXT, task.ticketId());
        return createMessage(chatId, text, KeyBoardCreator.createStartMarkup());
    }

    public static SendPhoto createQuestionWithImage(long chatId, Question question, List<Answer> answers, boolean isSelected) {
        return SendPhoto.builder()
                .photo(new InputFile(question.getImageURI()))
                .caption(question.getDescription() + "\n" + answers.stream()
                        .map(answer -> answer.getOrderInQuestion() + ". " + answer.getAnswerText() + "\n")
                        .collect(Collectors.joining()))
                .replyMarkup(KeyBoardCreator.createAnswersMarkup(answers, question, isSelected))
                .chatId(chatId)
                .build();
    }

    public static SendMessage createNotificationMessage(long chatId) {
        return createMessage(chatId, NOTIFICATION_TEXT, KeyBoardCreator.createToTicketsMarkup());
    }

    public static SendMessage createGreetingMessage(long chatId) {
        return createMessage(chatId, GREETINGS_TEXT, KeyBoardCreator.createToTicketsMarkup());
    }

    private static SendMessage createMessage(long chatId, String text, InlineKeyboardMarkup markup) {
        return SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .replyMarkup(markup)
                .build();
    }
}
