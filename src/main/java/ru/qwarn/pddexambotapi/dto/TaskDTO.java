package ru.qwarn.pddexambotapi.dto;

import ru.qwarn.pddexambotapi.models.TaskType;

public record TaskDTO(TaskType taskType, Integer ticketId) {
}


