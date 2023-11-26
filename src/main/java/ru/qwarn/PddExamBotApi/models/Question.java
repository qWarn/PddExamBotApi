package ru.qwarn.PddExamBotApi.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "question")
@NoArgsConstructor
@Setter
@Getter
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
    private int orderInThicket;

    @Column(name = "correct_answer_number")
    private int correctAnswerNumber;

    @Column(name = "correct_answer_explanation")
    private String correctAnswerExplanation;

    @ManyToOne
    @JoinColumn(name = "ticket_id" )
    private Ticket ticket;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "question")
    private List<Answer> answers = new ArrayList<>();

    @OneToMany(mappedBy = "question", fetch = FetchType.LAZY)
    private List<User> users = new ArrayList<>();

    @OneToMany(mappedBy = "question")
    private List<SelectedQuestions> selectedQuestions = new ArrayList<>();

    public Question(String imageURI, String description, int orderInThicket, int correctAnswerNumber, String correctAnswerDescription) {
        this.imageURI = imageURI;
        this.description = description;
        this.orderInThicket = orderInThicket;
        this.correctAnswerNumber = correctAnswerNumber;
        this.correctAnswerExplanation = correctAnswerDescription;
    }
}
