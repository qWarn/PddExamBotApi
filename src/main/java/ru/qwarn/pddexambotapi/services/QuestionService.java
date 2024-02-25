package ru.qwarn.pddexambotapi.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.qwarn.pddexambotapi.exceptions.QuestionNotFoundException;
import ru.qwarn.pddexambotapi.models.Question;
import ru.qwarn.pddexambotapi.repositories.QuestionRepository;



@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;

    public Question getFirstQuestion(int ticketId) {
        return questionRepository.findByTicketNumberAndOrderInTicket(ticketId, 1)
                .orElseThrow(() -> new QuestionNotFoundException(String.format("Question with number %d in ticket %d doesn't exists", 1, ticketId)));
    }

    public Question getById(int id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new QuestionNotFoundException(String.format("Question with id %d doesn't exists", id)));
    }

    @Transactional
    public Question saveQuestion(Question question) {
        return questionRepository.save(question);
    }

    @Transactional
    public void deleteAllQuestions() {
        questionRepository.deleteAll();
    }

}
