package ru.qwarn.pddexambotapi.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.qwarn.pddexambotapi.models.Answer;
import ru.qwarn.pddexambotapi.models.Question;
import ru.qwarn.pddexambotapi.repositories.AnswerRepository;

import java.util.List;


@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class AnswerService {

    private final AnswerRepository answerRepository;

    public List<Answer> getAnswers(Question question) {
        return answerRepository.findAllByQuestion(question);
    }

    @Transactional
    public Answer saveAnswer(Answer answer) {
        return answerRepository.save(answer);
    }

    @Transactional
    public void deleteAllAnswers() {
        answerRepository.deleteAll();
    }

}
