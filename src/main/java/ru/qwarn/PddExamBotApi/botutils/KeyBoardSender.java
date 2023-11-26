package ru.qwarn.PddExamBotApi.botutils;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ru.qwarn.PddExamBotApi.models.Answer;
import ru.qwarn.PddExamBotApi.models.Question;

import java.util.ArrayList;
import java.util.List;

@Component
public class KeyBoardSender {

    public InlineKeyboardMarkup getInlineKeyBoardMarkupForThickets(int from, int to){
        List<List<InlineKeyboardButton>> keyBoard = new ArrayList<>();
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        for (int i = from; i <= to; i++) {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(String.valueOf(i));
            button.setCallbackData("thicket " + i);
            buttons.add(button);
            if (i % 5 == 0){
                keyBoard.add(buttons);
                buttons = new ArrayList<>();
            }
        }
        InlineKeyboardButton selectedButton = new InlineKeyboardButton();
        selectedButton.setText("Избранное");
        selectedButton.setCallbackData("selected");
        keyBoard.add(List.of(selectedButton));

        InlineKeyboardButton nextThicketsButton = new InlineKeyboardButton();

        if (from == 1) {
            nextThicketsButton.setText("Далее");
            nextThicketsButton.setCallbackData("next");
        }else {
            nextThicketsButton.setText("Назад");
            nextThicketsButton.setCallbackData("prev");
        }

        keyBoard.add(List.of(nextThicketsButton));

        inlineKeyboardMarkup.setKeyboard(keyBoard);
        return inlineKeyboardMarkup;
    }

    public InlineKeyboardMarkup addQuestionToSelectedMarkup(Question question){
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        InlineKeyboardButton selectedButton = new InlineKeyboardButton();
        selectedButton.setText("В избранное");
        selectedButton.setCallbackData("add " + question.getId());
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        buttons.add(List.of(selectedButton));
        markup.setKeyboard(buttons);
        return markup;
    }

    public InlineKeyboardMarkup removeQuestionFromSelected(Question question){
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        InlineKeyboardButton selectedButton = new InlineKeyboardButton();
        selectedButton.setText("Убрать из избранного");
        selectedButton.setCallbackData("remove " + question.getId());
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        buttons.add(List.of(selectedButton));
        markup.setKeyboard(buttons);
        return markup;
    }

    public InlineKeyboardMarkup getInlineMarkupForFinishMessage(){
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        InlineKeyboardButton selectedButton = new InlineKeyboardButton();
        selectedButton.setText("Назад к билетам.");
        selectedButton.setCallbackData("backToTickets");
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        buttons.add(List.of(selectedButton));
        markup.setKeyboard(buttons);
        return markup;
    }

    public ReplyKeyboardMarkup getReplyKeyBoardMarkupForAnswers(List<Answer> answers){

        ReplyKeyboardMarkup markup1 = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();

        for (Answer answer : answers) {
            KeyboardRow row = new KeyboardRow();
            row.add(String.valueOf(answer.getOrderInQuestion()));
            keyboardRows.add(row);
        }
        KeyboardRow exitButton = new KeyboardRow();
        exitButton.add("Выйти");
        keyboardRows.add(exitButton);

        markup1.setKeyboard(keyboardRows);
        return markup1;
    }
}
