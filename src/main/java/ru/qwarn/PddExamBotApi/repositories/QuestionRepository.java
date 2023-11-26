package ru.qwarn.PddExamBotApi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.qwarn.PddExamBotApi.models.Question;


@Repository
public interface QuestionRepository extends JpaRepository<Question, Integer> {

    Question findByTicketIdAndOrderInThicket(int thicketId, int orderInThicket);


}
