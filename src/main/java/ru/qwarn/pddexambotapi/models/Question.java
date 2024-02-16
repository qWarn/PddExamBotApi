package ru.qwarn.pddexambotapi.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


@Entity
@Data
@NoArgsConstructor
public class Question {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "image_uri")
    private String imageURI;

    @Column(name = "description")
    private String description;

    @Column(name = "order_in_ticket")
    private int orderInTicket;

    @Column(name = "correct_answer_number")
    private int correctAnswerNumber;

    @Column(name = "correct_answer_explanation")
    private String correctAnswerExplanation;

    @Column(name = "fails_count")
    private long failsCount;


    @JoinColumn(name = "ticket_number" )
    private int ticketNumber;


    @OneToMany(fetch = FetchType.LAZY, mappedBy = "question")
    private List<Answer> answers = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "question", fetch = FetchType.LAZY)
    private List<User> users = new ArrayList<>();

    @OneToMany(mappedBy = "question")
    private List<Selected> selected = new ArrayList<>();

    public Question(String imageURI, String description, int orderInTicket, int correctAnswerNumber, String correctAnswerDescription) {
        this.imageURI = imageURI;
        this.description = description;
        this.orderInTicket = orderInTicket;
        this.correctAnswerNumber = correctAnswerNumber;
        this.correctAnswerExplanation = correctAnswerDescription;
    }
}
