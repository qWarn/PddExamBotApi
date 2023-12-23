package ru.qwarn.pddexambotapi.services;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.qwarn.pddexambotapi.exceptions.QuestionNotFoundException;
import ru.qwarn.pddexambotapi.exceptions.UserDoesntExistsException;
import ru.qwarn.pddexambotapi.models.Answer;
import ru.qwarn.pddexambotapi.models.Question;
import ru.qwarn.pddexambotapi.models.SelectedQuestions;
import ru.qwarn.pddexambotapi.models.User;
import ru.qwarn.pddexambotapi.repositories.QuestionRepository;
import ru.qwarn.pddexambotapi.utils.MessageCreator;

import java.rmi.UnexpectedException;
import java.util.List;
import java.util.Optional;


@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final UserService userService;
    private final AnswerService answerService;
    private final SelectedQuestionsService selectedQuestionsService;


    public Question findByTicketAndId(int ticketId, int orderInTicket) {
        Optional<Question> question = questionRepository.findByTicketNumberAndOrderInTicket(ticketId, orderInTicket);
        if (question.isEmpty()) {
            throw new QuestionNotFoundException(String.format("Question with number %d in ticket %d doesn't exists",
                    orderInTicket, ticketId));
        }
        return question.get();

    }

    public Question findById(int id) {
        Optional<Question> question = questionRepository.findById(id);
        if (question.isEmpty()) {
            throw new QuestionNotFoundException(String.format("Question with id %d doesn't exists", id));
        }
        return question.get();

    }

    @Transactional
    public void saveQuestion(Question question) throws UnexpectedException {
        if (question == null) {
            throw new UnexpectedException("Question is null");
        }
        questionRepository.save(question);
    }

    @Transactional
    public void deleteAllQuestions() {
        questionRepository.deleteAll();
    }

    public ResponseEntity<PartialBotApiMethod<Message>> getQuestionResponse(long chatId, Question question, User user) {
        user.setQuestion(question);
        List<Answer> answers = answerService.getQuestionAnswers(question.getId());

        if (question.getImageURI() == null) {
            return ResponseEntity.ok(MessageCreator.createQuestionWithoutMessage(chatId, question, answers));
        } else {
            return ResponseEntity.ok(MessageCreator.createQuestionWithImage(chatId, question, answers));
        }
    }


    @Transactional
    public ResponseEntity<PartialBotApiMethod<Message>> nextQuestion(long chatId) {

        User user = getUser(chatId);

        Question currentQuestion = user.getQuestion();

        if (user.getTaskType().equals("ticket")) {
            if (currentQuestion.getOrderInTicket() == 20) {
                return userService.getFinishMessage(chatId);
            }

            return getQuestionResponse(chatId, findById(currentQuestion.getId() + 1), user);
        } else {
            Optional<Question> questionOptional = selectedQuestionsService.nextSelectedQuestion(user);
            if (questionOptional.isEmpty()) {
                return userService.getFinishMessage(chatId);
            }

            return getQuestionResponse(chatId, questionOptional.get(), user);
        }
    }

    @Transactional
    public ResponseEntity<PartialBotApiMethod<Message>> getFirstSelectedQuestion(long chatId) {
        User user = getUser(chatId);

        user.setTaskType("selected");

        return getQuestionResponse(chatId,
                selectedQuestionsService.getFirstQuestionFromSelected(user),
                user);
    }

    private User getUser(long chatId) {
        Optional<User> userOptional = userService.findByChatId(chatId);

        if (userOptional.isEmpty()) {
            throw new UserDoesntExistsException();

        }
        return userOptional.get();
    }


    @Transactional
    public ResponseEntity<PartialBotApiMethod<Message>> getFirstQuestionFromTicket(long chatId, int ticketId) {
        User user = getUser(chatId);
        user.setTaskType("ticket");

        Question question = findByTicketAndId(ticketId, 1);

        return getQuestionResponse(chatId, question, user);
    }


    @Transactional
    public void removeQuestionFromSelected(long chatId, int questionId) {
        User user = getUser(chatId);
        Question question = findById(questionId);
        selectedQuestionsService.removeFromSelectedQuestions(question, user);
    }


    @Transactional
    public void addQuestionToSelected(long chatId, int questionId) {
        User user = getUser(chatId);
        Question question = findById(questionId);

        if (selectedQuestionsService.findByQuestionAndUser(question, user).isEmpty()) {
            SelectedQuestions selectedQuestion = new SelectedQuestions();
            selectedQuestion.setQuestion(question);
            selectedQuestion.setUser(user);
            selectedQuestionsService.saveQuestionToSelected(selectedQuestion);
        }
    }

}
