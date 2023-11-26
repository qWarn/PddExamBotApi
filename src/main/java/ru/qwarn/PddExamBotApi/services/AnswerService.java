package ru.qwarn.PddExamBotApi.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.qwarn.PddExamBotApi.models.Answer;
import ru.qwarn.PddExamBotApi.repositories.AnswerRepository;

import java.util.List;


@Service
@Transactional(readOnly = true)
public class AnswerService {

    private final AnswerRepository answerRepository;


    public AnswerService(AnswerRepository answerRepository) {
        this.answerRepository = answerRepository;
    }


    public List<Answer> getQuestionAnswers(int questionId){
        return answerRepository.getByQuestionId(questionId);
    }
}
