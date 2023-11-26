package ru.qwarn.PddExamBotApi.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.qwarn.PddExamBotApi.models.Ticket;
import ru.qwarn.PddExamBotApi.repositories.TicketRepository;


import java.util.List;

@Service
@Transactional(readOnly = true)
public class TicketService {

    private final TicketRepository ticketRepository;

    public TicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public List<Ticket> findAll(){
        return ticketRepository.findAll();
    }

    public Ticket findById(int id){
        return ticketRepository.findById(id).get();
    }
    @Transactional
    public void save(Ticket ticket){
        ticketRepository.save(ticket);
    }


}
