package ru.qwarn.pddexambotapi.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.qwarn.pddexambotapi.models.Answer;
import ru.qwarn.pddexambotapi.models.Question;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static ru.qwarn.pddexambotapi.constants.CallbackConstants.*;

@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class KeyBoardCreator {

    protected static InlineKeyboardMarkup createTicketsMarkup(boolean next) {
        int from = next ? 21 : 1;
        int to = from + 20;

        List<List<InlineKeyboardButton>> ticketsKeyboard = new ArrayList<>();
        List<InlineKeyboardButton> ticketsRow = new ArrayList<>();

        IntStream.range(from, to).forEach(i -> {
            addKeyToRow(ticketsRow, String.valueOf(i), START_TICKET + " " + i);
            if (i % 5 == 0) {
                ticketsKeyboard.add(new ArrayList<>(ticketsRow));
                ticketsRow.clear();
            }
        });

        return InlineKeyboardMarkup.builder()
                .keyboard(ticketsKeyboard)
                .keyboardRow(addKeyToRow(new ArrayList<>(), "Избранное", START_SELECTED))
                .keyboardRow(addKeyToRow(new ArrayList<>(), !next ? "Далее" : "Назад", !next ? GET_MORE_TICKETS : GET_TICKETS))
                .build();
    }

    protected static InlineKeyboardMarkup createToTicketsMarkup() {
        return InlineKeyboardMarkup.builder()
                .keyboardRow(addKeyToRow(new ArrayList<>(), "К билетам.", GET_TICKETS)).build();
    }

    protected static InlineKeyboardMarkup createCorrectAnswerMarkup(){
        return InlineKeyboardMarkup.builder()
                .keyboardRow(addKeyToRow(new ArrayList<>(), "Назад", GET_QUESTION)).build();
    }

    protected static InlineKeyboardMarkup createAnswersMarkup(List<Answer> answers, Question question, boolean isSelected) {
        List<InlineKeyboardButton> answerRow = new ArrayList<>();
        List<InlineKeyboardButton> specialRow = new ArrayList<>();
        List<InlineKeyboardButton> returnRow = new ArrayList<>();

        answers.forEach(answer ->
                addKeyToRow(answerRow, String.valueOf(answer.getOrderInQuestion()), GET_QUESTION + " " + answer.getOrderInQuestion())
        );

        addKeyToRow(specialRow, "Правильный ответ", GET_ANSWER);
        addKeyToRow(specialRow, isSelected ? "Убрать из избранного" : "В избранное", isSelected ?
                REMOVE_FROM_SELECTED + " " + question.getId() : ADD_TO_SELECTED + " " + question.getId());
        addKeyToRow(returnRow, "К билетам", GET_TICKETS);

        return InlineKeyboardMarkup.builder()
                .keyboardRow(answerRow)
                .keyboardRow(specialRow)
                .keyboardRow(returnRow)
                .build();
    }

    protected static InlineKeyboardMarkup createStartMarkup(){
        return InlineKeyboardMarkup.builder()
                .keyboardRow(addKeyToRow(new ArrayList<>(), "Старт!", GET_QUESTION)).build();
    }

    private static List<InlineKeyboardButton> addKeyToRow(List<InlineKeyboardButton> row, String text, String callback){
       row.add(InlineKeyboardButton.builder().text(text).callbackData(callback).build());
       return row;
    }
}
