package ru.qwarn.pddexambotapi.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@Data
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String answerText;

    private int orderInQuestion;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "question_id")
    private Question question;


    public Answer(String answer, int orderInQuestion) {
        this.answerText = answer;
        this.orderInQuestion = orderInQuestion;
    }

}
