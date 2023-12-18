package ru.qwarn.pddexambotapi.services;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.qwarn.pddexambotapi.models.Question;
import ru.qwarn.pddexambotapi.models.User;
import ru.qwarn.pddexambotapi.repositories.QuestionRepository;
import ru.qwarn.pddexambotapi.utils.MessageCreator;

import java.rmi.UnexpectedException;
import java.util.Optional;


@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;

    private final UserService userService;

    private final MessageCreator messageCreator;


    public Question findByTicketAndId(int ticketId, int orderInTicket) throws UnexpectedException {
        Optional<Question> question = questionRepository.findByTicketNumberAndOrderInTicket(ticketId, orderInTicket);
        if (question.isEmpty()){
            throw new UnexpectedException("Question with number " + orderInTicket + " in ticket " + ticketId  + " doesn't exists");
        }
        return question.get();

    }

    public Question findById(int id) throws UnexpectedException {
        Optional<Question> question = questionRepository.findById(id);
        if (question.isEmpty()){
            throw new UnexpectedException("Question with id " + id + " doesn't exists");
        }
        return question.get();

    }

    @Transactional
    public void saveQuestion(Question question) throws UnexpectedException {
        if (question == null){
            throw new UnexpectedException("Question is null");
        }
        questionRepository.save(question);
    }

    @Transactional
    public void deleteAllQuestions(){
        questionRepository.deleteAll();
    }

    @Transactional
    public ResponseEntity<PartialBotApiMethod<Message>> getQuestionResponse(long chatId, Question question, User user) throws UnexpectedException {
        user.setQuestion(question);
        userService.save(user);

        if (question.getImageURI() == null){
            return new ResponseEntity<>(messageCreator.createQuestionWithoutMessage(chatId, question), HttpStatus.OK);
        }else {
            return new ResponseEntity<>(messageCreator.createQuestionWithImage(chatId, question), HttpStatus.OK);
        }
    }

}
