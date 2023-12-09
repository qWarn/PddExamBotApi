package ru.qwarn.PddExamBotApi.services;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Limit;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.qwarn.PddExamBotApi.models.Question;
import ru.qwarn.PddExamBotApi.models.User;
import ru.qwarn.PddExamBotApi.repositories.QuestionRepository;
import ru.qwarn.PddExamBotApi.utils.MessageCreator;

import java.util.List;


@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;

    private final UserService userService;

    private final MessageCreator messageCreator;


    public Question findByTicketAndId(int thicketId, int orderInThicket){
        return questionRepository.findByTicketNumberAndOrderInTicket(thicketId, orderInThicket);
    }

    public Question findById(int id){
        return questionRepository.findById(id).get();
    }

    @Transactional
    public void saveQuestion(Question question) {
        questionRepository.save(question);
    }

    @Transactional
    public void deleteAllQuestions(){
        questionRepository.deleteAll();
    }

    @Transactional
    public ResponseEntity<?> getQuestionResponse(long chatId, Question question, User user){
        user.setQuestion(question);
        userService.save(user);

        if (question.getImageURI() == null){
            return new ResponseEntity<>(messageCreator.createQuestionWithoutMessage(chatId, question), HttpStatus.OK);
        }else {
            return new ResponseEntity<>(messageCreator.createQuestionWithImage(chatId, question), HttpStatus.OK);
        }
    }

}
