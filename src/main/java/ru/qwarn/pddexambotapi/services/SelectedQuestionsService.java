package ru.qwarn.pddexambotapi.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.qwarn.pddexambotapi.exceptions.SelectedQuestionsIsEmptyException;
import ru.qwarn.pddexambotapi.models.Question;
import ru.qwarn.pddexambotapi.models.SelectedQuestions;
import ru.qwarn.pddexambotapi.models.User;
import ru.qwarn.pddexambotapi.repositories.SelectedQuestionsRepository;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class SelectedQuestionsService {

    private final Random random;

    private final SelectedQuestionsRepository selectedQuestionsRepository;

    @Transactional
    public void saveQuestionToSelected(SelectedQuestions selectedQuestions) {
        selectedQuestionsRepository.save(selectedQuestions);
    }

    public Optional<SelectedQuestions> findByQuestionAndUser(Question question, User user) {
        return selectedQuestionsRepository.findByQuestionAndUser(question, user);
    }

    @Transactional
    public void removeFromSelectedQuestions(Question question, User user) {
        selectedQuestionsRepository.removeByQuestionAndUser(question, user);
    }

    public List<SelectedQuestions> findAllByUserAndAlreadyWasFalse(User user) {
        return selectedQuestionsRepository.findAllByUserAndAlreadyWasFalse(user);
    }

    public List<SelectedQuestions> findAllByUser(User user) {
        return selectedQuestionsRepository.findAllByUser(user);
    }


    @Transactional
    public void save(SelectedQuestions selectedQuestions) {
        selectedQuestionsRepository.save(selectedQuestions);
    }

    public Question getRandomQuestion(List<SelectedQuestions> selectedQuestions) {
        SelectedQuestions selectedQuestion = selectedQuestions.get(
                selectedQuestions.size() == 1 ? 0 : random.nextInt(selectedQuestions.size() - 1));
        selectedQuestion.setAlreadyWas(true);
        save(selectedQuestion);
        return selectedQuestion.getQuestion();
    }

    public Optional<Question> nextSelectedQuestion(User user) {
        List<SelectedQuestions> selectedQuestions = findAllByUserAndAlreadyWasFalse(user);
        if (selectedQuestions.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(getRandomQuestion(selectedQuestions));
    }

    public Question getFirstQuestionFromSelected(User user) {
        List<SelectedQuestions> selectedQuestions = findAllByUser(user);
        if (selectedQuestions.isEmpty()) {
            throw new SelectedQuestionsIsEmptyException("Список избранных пуст! Вам следует добавить хоть 1 вопрос в избранное!");
        }

        selectedQuestions.forEach(question -> {
            question.setAlreadyWas(false);
            save(question);
        });

        return getRandomQuestion(selectedQuestions);
    }

}
