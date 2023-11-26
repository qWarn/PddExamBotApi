package ru.qwarn.PddExamBotApi.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ticket")
@NoArgsConstructor
@Getter
@Setter
public class Ticket {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "number_of_ticket")
    private int numberOfTicket;

    @OneToMany(mappedBy = "ticket", fetch = FetchType.LAZY)
    private List<Question> questions = new ArrayList<>();

    @OneToMany(mappedBy = "ticket", fetch = FetchType.EAGER)
    private List<User> users = new ArrayList<>();

    public Ticket(int numberOfTicket) {
        this.numberOfTicket = numberOfTicket;
    }
}
