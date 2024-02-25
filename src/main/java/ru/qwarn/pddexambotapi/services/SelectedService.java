package ru.qwarn.pddexambotapi.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.qwarn.pddexambotapi.exceptions.SelectedIsEmptyException;
import ru.qwarn.pddexambotapi.models.Question;
import ru.qwarn.pddexambotapi.models.Selected;
import ru.qwarn.pddexambotapi.models.SelectedId;
import ru.qwarn.pddexambotapi.models.User;
import ru.qwarn.pddexambotapi.repositories.SelectedRepository;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class SelectedService {

    private final Random random;
    private final SelectedRepository selectedRepository;

    public Optional<Selected> getById(long chatId, int questionId) {
        return selectedRepository.findById(new SelectedId(chatId, questionId));
    }

    public List<Selected> getUnresolvedQuestions(User user) {
        return selectedRepository.findAllByUserAndAlreadyWasFalse(user);
    }

    @Transactional
    public void removeFromSelected(User user, Question question) {
        selectedRepository.deleteById(new SelectedId(user.getChatId(), question.getId()));
    }

    public List<Selected> getAllByUser(User user) {
        return selectedRepository.findAllByUser(user);
    }


    @Transactional
    public Optional<Question> getQuestionFromSelected(User user) {
        List<Selected> selectedQuestions = getUnresolvedQuestions(user);
        if (selectedQuestions.isEmpty()) {
            return Optional.empty();
        }

        Selected selectedQuestion =
                selectedQuestions.get(selectedQuestions.size() == 1 ? 0 : random.nextInt(selectedQuestions.size() - 1));

        return Optional.of(selectedQuestion.getQuestion());
    }

    @Transactional
    public void refreshSelectedQuestions(User user) {
        List<Selected> selectedQuestions = getAllByUser(user);
        if (selectedQuestions.isEmpty()) {
            throw new SelectedIsEmptyException("Список избранных пуст! Вам следует добавить хоть 1 вопрос в избранное!");
        }

        selectedQuestions.forEach(question -> question.setAlreadyWas(false));
    }

    @Transactional
    public void save(User user, Question question) {
        if (getById(user.getChatId(), question.getId()).isEmpty()) {
            Selected selectedQuestion = new Selected();
            selectedQuestion.setQuestion(question);
            selectedQuestion.setUser(user);
            selectedRepository.save(selectedQuestion);
        }
    }

}
