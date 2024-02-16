package ru.qwarn.pddexambotapi.models;

import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

@Embeddable
@Data
public class SelectedId implements Serializable {
    long userId;
    int questionId;
}
