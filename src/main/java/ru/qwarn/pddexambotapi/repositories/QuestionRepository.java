package ru.qwarn.pddexambotapi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.qwarn.pddexambotapi.models.Question;

import java.util.Optional;


@Repository
public interface QuestionRepository extends JpaRepository<Question, Integer> {

    Optional<Question> findByTicketNumberAndOrderInTicket(int thicketId, int orderInThicket);


}
