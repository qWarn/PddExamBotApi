package ru.qwarn.pddexambotapi.models;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.util.List;

@Entity(name = "\"user\"")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    private long chatId;

    private int failsCount;

    @Enumerated(EnumType.STRING)
    private TaskType taskType;

    private Timestamp lastActive;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "current_question_id")
    private Question question;

    @OneToMany(mappedBy = "user")
    private List<Selected> selected;

    public User(long chatId) {
        this.chatId = chatId;
    }
}
