package ru.qwarn.PddExamBotApi.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "answer")
@NoArgsConstructor
@Getter
@Setter
public class Answer {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "answer_text")
    private String answerText;

    @Column(name = "order_in_question")
    private int orderInQuestion;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "question_id" )
    private Question question;


    public Answer(String answer, int orderInQuestion) {
        this.answerText = answer;
        this.orderInQuestion = orderInQuestion;
    }

}
