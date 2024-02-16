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
    @Column(name = "chat_id")
    private long chatId;

    @Column(name = "fails_count")
    private int failsCount;

    @Column(name = "task_type")
    private String taskType;

    @Column(name = "last_active")
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
