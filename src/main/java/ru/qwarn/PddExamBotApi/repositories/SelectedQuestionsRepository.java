package ru.qwarn.PddExamBotApi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.qwarn.PddExamBotApi.models.Question;
import ru.qwarn.PddExamBotApi.models.SelectedQuestions;
import ru.qwarn.PddExamBotApi.models.User;


import java.util.List;
import java.util.Optional;

@Repository
public interface SelectedQuestionsRepository extends JpaRepository<SelectedQuestions, Integer> {

    Optional<SelectedQuestions> findByQuestionAndUser(Question question, User user);

    void removeByQuestionAndUser(Question question, User user);

    List<SelectedQuestions> findAllByUserAndAlreadyWasFalse(User user);

    List<SelectedQuestions> findAllByUser(User user);

}
