package ru.qwarn.pddexambotapi.models;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Data
@NoArgsConstructor
public class Selected {

    @EmbeddedId
    private SelectedId selectedId = new SelectedId();

    @ToString.Exclude
    @MapsId("userId")
    @ManyToOne
    private User user;

    @ToString.Exclude
    @MapsId("questionId")
    @ManyToOne
    private Question question;

    private boolean alreadyWas;

}
