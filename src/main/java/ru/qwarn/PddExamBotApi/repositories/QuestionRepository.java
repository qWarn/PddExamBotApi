package ru.qwarn.PddExamBotApi.repositories;

import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.qwarn.PddExamBotApi.models.Question;

import java.util.List;


@Repository
public interface QuestionRepository extends JpaRepository<Question, Integer> {

    Question findByTicketNumberAndOrderInTicket(int thicketId, int orderInThicket);

    @Query(value = "select * from Question q order by q.failsCount", nativeQuery = true)
    List<Question> find20GroupByFails(Limit limit);


}
