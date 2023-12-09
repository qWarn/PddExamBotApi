package ru.qwarn.PddExamBotApi.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.qwarn.PddExamBotApi.models.Question;
import ru.qwarn.PddExamBotApi.models.SelectedQuestions;
import ru.qwarn.PddExamBotApi.models.User;
import ru.qwarn.PddExamBotApi.repositories.SelectedQuestionsRepository;


import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class SelectedQuestionsService {

    private final SelectedQuestionsRepository selectedQuestionsRepository;

    @Transactional
    public void saveQuestionToSelected(SelectedQuestions selectedQuestions){
        selectedQuestionsRepository.save(selectedQuestions);
    }

    public Optional<SelectedQuestions> findByQuestionAndUser(Question question, User user){
        return selectedQuestionsRepository.findByQuestionAndUser(question, user);
    }

    @Transactional
    public void removeFromSelectedQuestions(Question question, User user){
        selectedQuestionsRepository.removeByQuestionAndUser(question, user);
    }

    public List<SelectedQuestions> findAllByUserAndAlreadyWasFalse(User user){
        return selectedQuestionsRepository.findAllByUserAndAlreadyWasFalse(user);
    }

    public List<SelectedQuestions> findAllByUser(User user){
        return selectedQuestionsRepository.findAllByUser(user);
    }


    @Transactional
    public void save(SelectedQuestions selectedQuestions){
        selectedQuestionsRepository.save(selectedQuestions);
    }

    public Question getNextQuestionFromSelected(List<SelectedQuestions> selectedQuestions){
        SelectedQuestions selectedQuestion = selectedQuestions.get(
                selectedQuestions.size() == 1 ? 0 : new Random().nextInt(selectedQuestions.size()-1));
        selectedQuestion.setAlreadyWas(true);
        save(selectedQuestion);
        return selectedQuestion.getQuestion();
    }

}
