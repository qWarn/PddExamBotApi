package ru.qwarn.pddexambotapi.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.qwarn.pddexambotapi.models.Answer;
import ru.qwarn.pddexambotapi.repositories.AnswerRepository;

import java.rmi.UnexpectedException;
import java.util.List;


@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class AnswerService {

    private final AnswerRepository answerRepository;

    public List<Answer> getQuestionAnswers(int questionId) throws UnexpectedException {
        List<Answer> answers = answerRepository.getByQuestionId(questionId);
        if (answers.isEmpty()){
            throw new UnexpectedException("List of entities is empty.");
        }
        return answers;

    }
    @Transactional
    public void saveAnswer(Answer answer) throws UnexpectedException {
        if (answer == null){
            throw new UnexpectedException("Answer is null.");
        }

        answerRepository.save(answer);
    }

    @Transactional
    public void deleteAllAnswers(){
        answerRepository.deleteAll();
    }

}
