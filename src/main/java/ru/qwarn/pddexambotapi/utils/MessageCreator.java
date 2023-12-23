package ru.qwarn.pddexambotapi.utils;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import ru.qwarn.pddexambotapi.models.Answer;
import ru.qwarn.pddexambotapi.models.Question;
import ru.qwarn.pddexambotapi.models.SelectedQuestions;
import ru.qwarn.pddexambotapi.models.User;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MessageCreator {


    private static final String FINISH_MESSAGE_TEXT = "Вы совершили %d %s";

    public static SendMessage createTicketsMessage(User user, int from, int to) {
        return SendMessage.builder()
                .chatId(user.getChatId())
                .text("Выберите номер билета для тренировки:")
                .replyMarkup(KeyBoardCreator.createInlineKeyBoardMarkupForTickets(from, to))
                .build();
    }

    public static SendMessage createAnswer(long chatId, Question question,
                                           Optional<SelectedQuestions> selectedQuestion,
                                           boolean isCorrect) {

        String text = (isCorrect ? "Вы ответили правильно!" + "\n" : "Вы ответили неправильно!" + "\n") +
                "Правильный ответ: " +
                question.getCorrectAnswerNumber() + "\n" +
                question.getCorrectAnswerExplanation();

        return SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .replyMarkup(selectedQuestion.isPresent() ?
                        KeyBoardCreator.createRemoveQuestionFromSelectedMarkup(question)
                        : KeyBoardCreator.createAddQuestionToSelectedMarkup(question))
                .build();
    }

    public static SendMessage createFinishMessage(long chatId, int fails) {
        return SendMessage.builder().text(getFinishMessageText(fails))
                .replyMarkup(KeyBoardCreator.createInlineMarkupForFinishMessage())
                .chatId(chatId)
                .build();
    }

    public static SendMessage createQuestionWithoutMessage(long chatId, Question question, List<Answer> answers) {

        return SendMessage.builder()
                .text(question.getDescription() + "\n" + answers.stream()
                        .map(answer -> answer.getOrderInQuestion() + ". " + answer.getAnswerText() + "\n")
                        .collect(Collectors.joining())).replyMarkup(KeyBoardCreator.createReplyKeyBoardMarkupForAnswers(answers))
                .chatId(chatId)
                .build();
    }

    @SneakyThrows
    public static SendPhoto createQuestionWithImage(long chatId, Question question, List<Answer> answers) {

        return SendPhoto.builder()
                .photo(new InputFile(question.getImageURI()))
                .caption(question.getDescription() + "\n" + answers.stream()
                        .map(answer -> answer.getOrderInQuestion() + ". " + answer.getAnswerText() + "\n")
                        .collect(Collectors.joining()))
                .replyMarkup(KeyBoardCreator.createReplyKeyBoardMarkupForAnswers(answers))
                .chatId(chatId)
                .build();
    }

    private static String getFinishMessageText(int failsCount) {
        if (failsCount == 0 || failsCount > 5) {
            return String.format(FINISH_MESSAGE_TEXT, failsCount, "ошибок.");
        }
        return
                String.format(FINISH_MESSAGE_TEXT,
                        failsCount,
                        failsCount == 1 ? "ошибку." : "ошибки.");
    }

    public SendMessage createMessageForNewUser(long chatId) {
        return SendMessage.builder().chatId(chatId)
                .text("Привет! \n Этот телеграм бот создан для подготовки к экзамену ПДД. \n Он содержит все 40 актуальных билетов на 2023 год.")
                .chatId(chatId)
                .replyMarkup(KeyBoardCreator.createInlineMarkupForFinishMessage())
                .build();
    }
}
