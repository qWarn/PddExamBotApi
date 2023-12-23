package ru.qwarn.pddexambotapi.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ru.qwarn.pddexambotapi.models.Answer;
import ru.qwarn.pddexambotapi.models.Question;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class KeyBoardCreator {


    protected static InlineKeyboardMarkup createInlineKeyBoardMarkupForTickets(int from, int to) {
        List<List<InlineKeyboardButton>> keyBoard = new ArrayList<>();
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        IntStream.range(from, to).forEach(i -> {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(String.valueOf(i));
            button.setCallbackData("ticket " + i);
            buttons.add(button);
            if (i % 5 == 0) {
                keyBoard.add(new ArrayList<>(buttons));
                buttons.clear();
            }
        });

        InlineKeyboardButton selectedButton = new InlineKeyboardButton();
        selectedButton.setText("Избранное");
        selectedButton.setCallbackData("selected");
        keyBoard.add(List.of(selectedButton));

        InlineKeyboardButton nextThicketsButton = new InlineKeyboardButton();

        if (from == 1) {
            nextThicketsButton.setText("Далее");
            nextThicketsButton.setCallbackData("nextTickets");
        } else {
            nextThicketsButton.setText("Назад");
            nextThicketsButton.setCallbackData("prevTickets");
        }

        keyBoard.add(List.of(nextThicketsButton));

        inlineKeyboardMarkup.setKeyboard(keyBoard);
        return inlineKeyboardMarkup;
    }

    protected static InlineKeyboardMarkup createAddQuestionToSelectedMarkup(Question question) {
        return getInlineKeyboardMarkup("В избранное", "addToSelected " + question.getId());
    }

    protected static InlineKeyboardMarkup createRemoveQuestionFromSelectedMarkup(Question question) {
        return getInlineKeyboardMarkup("Убрать из избранного", "removeFromSelected " + question.getId());
    }

    protected static InlineKeyboardMarkup createInlineMarkupForFinishMessage() {
        return getInlineKeyboardMarkup("К билетам.", "backToTickets");
    }

    private static InlineKeyboardMarkup getInlineKeyboardMarkup(String text, String backToTickets) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        InlineKeyboardButton selectedButton = new InlineKeyboardButton();
        selectedButton.setText(text);
        selectedButton.setCallbackData(backToTickets);
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        buttons.add(List.of(selectedButton));
        markup.setKeyboard(buttons);
        return markup;
    }

    protected static ReplyKeyboardMarkup createReplyKeyBoardMarkupForAnswers(List<Answer> answers) {

        ReplyKeyboardMarkup markup1 = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();

        answers.forEach(answer -> {
            KeyboardRow row = new KeyboardRow();
            row.add(String.valueOf(answer.getOrderInQuestion()));
            keyboardRows.add(row);
        });

        KeyboardRow exitButton = new KeyboardRow();
        exitButton.add("Выйти");
        keyboardRows.add(exitButton);

        markup1.setKeyboard(keyboardRows);
        return markup1;
    }
}
