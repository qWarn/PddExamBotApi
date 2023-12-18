package ru.qwarn.pddexambotapi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.qwarn.pddexambotapi.models.Answer;


import java.util.List;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Integer> {
    List<Answer> getByQuestionId(int questionId);
}
