package ru.qwarn.PddExamBotApi.utils;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import ru.qwarn.PddExamBotApi.exceptions.InvalidAnswerException;
import ru.qwarn.PddExamBotApi.models.Answer;
import ru.qwarn.PddExamBotApi.models.Question;
import ru.qwarn.PddExamBotApi.models.User;
import ru.qwarn.PddExamBotApi.services.AnswerService;
import ru.qwarn.PddExamBotApi.services.SelectedQuestionsService;
import ru.qwarn.PddExamBotApi.services.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class MessageCreator {

    private final UserService userService;
    private final KeyBoardCreator keyBoardCreator;
    private final SelectedQuestionsService selectedQuestionsService;
    private final AnswerService answerService;

    public SendMessage createTicketsMessage(User user, int from, int to) {
        user.setTaskType("none");
        user.setQuestion(null);
        user.setFailsCount(0);
        userService.save(user);

        return SendMessage.builder()
                .chatId(user.getChatId())
                .text("Выберите номер билета для тренировки:")
                .replyMarkup(keyBoardCreator.createInlineKeyBoardMarkupForTickets(from, to))
                .build();
    }

    public SendMessage createAnswer(long chatId, int answerNumber, Question question, User user){
        if (answerNumber > answerService.getQuestionAnswers(question.getId()).size() || answerNumber <= 0){
            throw new InvalidAnswerException("Ответ под номером " + answerNumber + " не существует!");
        }

        boolean isUserAnswerCorrect = question.getCorrectAnswerNumber() == answerNumber;

        user.setFailsCount(user.getFailsCount() + (isUserAnswerCorrect ? 0 : 1));
        userService.save(user);

        String text = (isUserAnswerCorrect ? "Вы ответили правильно!" + "\n" : "Вы ответили неправильно!" + "\n") +
                "Правильный ответ: " +
                question.getCorrectAnswerNumber() + "\n" +
                question.getCorrectAnswerExplanation();

        return SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .replyMarkup(selectedQuestionsService.findByQuestionAndUser(question, user).isPresent() ?
                        keyBoardCreator.createRemoveQuestionFromSelectedMarkup(question) : keyBoardCreator.createAddQuestionToSelectedMarkup(question))
                .build();
    }

    public SendMessage createFinishMessage(long chatId, User user){
        user.setQuestion(null);
        userService.save(user);
        int fails = user.getFailsCount();

        return SendMessage.builder().text(fails > 4 || fails == 0? "Вы совершили " + user.getFailsCount() + " ошибок."
                    : fails == 1? "Вы совершили " + user.getFailsCount() + " ошибку."
                        : "Вы совершили " + user.getFailsCount() + " ошибки.")
                .replyMarkup(keyBoardCreator.createInlineMarkupForFinishMessage())
                .chatId(chatId)
                .build();
    }

    public SendMessage createQuestionWithoutMessage(long chatId, Question question) {
        List<Answer> answers = answerService.getQuestionAnswers(question.getId());

        return SendMessage.builder()
                .text(question.getDescription() + "\n" +  answers.stream()
                      .map(answer -> answer.getOrderInQuestion() + ". " + answer.getAnswerText() + "\n")
                      .collect(Collectors.joining())).replyMarkup(keyBoardCreator.createReplyKeyBoardMarkupForAnswers(answers))
                .chatId(chatId)
                .build();
    }

    @SneakyThrows
    public SendPhoto createQuestionWithImage(long chatId, Question question){
        List<Answer> answers = answerService.getQuestionAnswers(question.getId());

        return  SendPhoto.builder()
                .photo(new InputFile(question.getImageURI()))
                .caption(question.getDescription() + "\n" +  answers.stream()
                        .map(answer -> answer.getOrderInQuestion() + ". " + answer.getAnswerText() + "\n")
                        .collect(Collectors.joining()))
                .replyMarkup(keyBoardCreator.createReplyKeyBoardMarkupForAnswers(answers))
                .chatId(chatId)
                .build();
    }

    public SendMessage createMessageForNewUser(long chatId){
        return SendMessage.builder().chatId(chatId)
                .text("Привет! \n Этот телеграм бот создан для подготовки к экзамену ПДД. \n Он содержит все 40 актуальных билетов на 2023 год.")
                .chatId(chatId)
                .replyMarkup(keyBoardCreator.createInlineMarkupForFinishMessage())
                .build();
    }
}
