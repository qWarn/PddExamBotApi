package ru.qwarn.PddExamBotApi.botutils;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import ru.qwarn.PddExamBotApi.exceptions.InvalidAnswerException;
import ru.qwarn.PddExamBotApi.models.Answer;
import ru.qwarn.PddExamBotApi.models.Question;
import ru.qwarn.PddExamBotApi.models.SelectedQuestions;
import ru.qwarn.PddExamBotApi.models.User;
import ru.qwarn.PddExamBotApi.repositories.SelectedQuestionsRepository;
import ru.qwarn.PddExamBotApi.services.AnswerService;
import ru.qwarn.PddExamBotApi.services.UserService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class MessageSender {

    private final UserService userService;

    private final KeyBoardSender keyBoardSender;
    private final SelectedQuestionsRepository selectedListService;

    private final AnswerService answerService;

    @Autowired
    public MessageSender(UserService userService, KeyBoardSender keyBoardSender, SelectedQuestionsRepository selectedListService, AnswerService answerService) {
        this.userService = userService;
        this.keyBoardSender = keyBoardSender;
        this.selectedListService = selectedListService;
        this.answerService = answerService;
    }

    public SendMessage sendThickets(User user, int from, int to) {
        //мб перенести добавления пользователя сюда | Думаю между patch и post
        user.setTicket(null);
        user.setQuestion(null);
        user.setFailsCount(0);
        userService.save(user);

        SendMessage message = new SendMessage();
        message.setChatId(user.getChatId());
        message.setText("Выбирете номер билета для тренировки:");
        message.setReplyMarkup(keyBoardSender.getInlineKeyBoardMarkupForThickets(from, to));

        return message;
    }

    public SendMessage sendUserAnswerAndGetCorrect(long chatId, int answerNumber, Question question, User user){
        SendMessage sendMessage = new SendMessage();

        StringBuilder text = new StringBuilder();
        if (question.getCorrectAnswerNumber() == answerNumber){
            text.append("Вы ответили правильно!" + "\n");
        }else {
            if (answerNumber > answerService.getQuestionAnswers(question.getId()).size()){ //TODO изменить запрос в сервисе, чтобы при вызове getQuestionAnswer возвращалось только кол-во ответов, ЕСЛИ ЭТОТ МЕТОД НИГДЕ БОЛЬШЕ НЕ ИСПОЛЬЗУЕТСЯ
                throw new InvalidAnswerException("Ответ под номером " + answerNumber + " не существует!");
            }
            text.append("Вы ответили неправильно!" + "\n");
            user.setFailsCount(user.getFailsCount()+1);
        }
        text.append("Правильный ответ: ").append(question.getCorrectAnswerNumber()).append("\n").append(question.getCorrectAnswerExplanation());

        sendMessage.setText(text.toString());
        sendMessage.setChatId(chatId);

        Optional<SelectedQuestions> selectedList = selectedListService.findByQuestionAndUser(question, user);
        if (selectedList.isPresent()){
            sendMessage.setReplyMarkup(keyBoardSender.removeQuestionFromSelected(question));
        }else {
            sendMessage.setReplyMarkup(keyBoardSender.addQuestionToSelectedMarkup(question));
        }

        return sendMessage;
    }

    public SendMessage sendFinishMessage(long chatId, User user){

        user.setQuestion(null);
        userService.save(user);

        SendMessage message = new SendMessage();
        int fails = user.getFailsCount();

        if (fails == 1) {
            message.setText("Вы совершили " + user.getFailsCount() + " ошибку.");
        }else if (fails > 1 && fails < 4){
            message.setText("Вы совершили " + user.getFailsCount() + " ошибки.");
        }else {
            message.setText("Вы совершили " + user.getFailsCount() + " ошибок.");
        }

        message.setReplyMarkup(keyBoardSender.getInlineMarkupForFinishMessage());
        message.setChatId(chatId);

        return message;
    }

    public SendMessage sendQuestionWithoutImage(long chatId, Question question) {
        List<Answer> answers = answerService.getQuestionAnswers(question.getId());
        SendMessage message = new SendMessage();
        message.setText(question.getDescription() + "\n" +  answers.stream()
                .map(answer -> answer.getOrderInQuestion() + ". " + answer.getAnswerText() + "\n")
                .collect(Collectors.joining()));
        message.setReplyMarkup(keyBoardSender.getReplyKeyBoardMarkupForAnswers(answers));
        message.setChatId(chatId);

        return message;
    }

    @SneakyThrows
    public SendPhoto sendQuestionWithImage(long chatId, Question question){
        List<Answer> answers = answerService.getQuestionAnswers(question.getId());
        SendPhoto photo = new SendPhoto();
        photo.setPhoto(new InputFile(question.getImageURI()));
        photo.setCaption(question.getDescription() + "\n" +  answers.stream()
                .map(answer -> answer.getOrderInQuestion() + ". " + answer.getAnswerText() + "\n")
                .collect(Collectors.joining()));
        photo.setReplyMarkup(keyBoardSender.getReplyKeyBoardMarkupForAnswers(answers));
        photo.setChatId(chatId);

        return photo;
    }
}
