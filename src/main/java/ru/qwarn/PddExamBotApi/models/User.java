package ru.qwarn.PddExamBotApi.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "telegram_user")
@Setter
@Getter
@NoArgsConstructor
public class User {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "chat_id")
    private long chatId;
    @Column(name = "fails_count")
    private int failsCount;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "current_ticket_id")
    private Ticket ticket;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "current_question_id")
    private Question question;

    @OneToMany(mappedBy = "user")
    private List<SelectedQuestions> selectedQuestions;

    public User(long chatId) {
        this.chatId = chatId;
    }
}
