package ru.qwarn.PddExamBotApi.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.qwarn.PddExamBotApi.models.Question;
import ru.qwarn.PddExamBotApi.repositories.QuestionRepository;


@Service
@Transactional(readOnly = true)
public class QuestionService {

    private final QuestionRepository questionRepository;

    public QuestionService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    public Question findByThicketAndId(int thicketId, int orderInThicket){
        return questionRepository.findByTicketIdAndOrderInThicket(thicketId, orderInThicket);
    }

    public Question findById(int id){
        return questionRepository.findById(id).get();
    }




}
