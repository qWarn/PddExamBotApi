package ru.qwarn.PddExamBotApi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.qwarn.PddExamBotApi.models.Ticket;


@Repository
public interface TicketRepository extends JpaRepository<Ticket, Integer> {


}
