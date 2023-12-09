package ru.qwarn.PddExamBotApi.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.qwarn.PddExamBotApi.models.Answer;
import ru.qwarn.PddExamBotApi.repositories.AnswerRepository;

import java.util.List;


@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class AnswerService {

    private final AnswerRepository answerRepository;

    public List<Answer> getQuestionAnswers(int questionId){
        return answerRepository.getByQuestionId(questionId);
    }
    @Transactional
    public void saveAnswer(Answer answer){
        answerRepository.save(answer);
    }

    @Transactional
    public void deleteAllAnswers(){
        answerRepository.deleteAll();
    }

}
