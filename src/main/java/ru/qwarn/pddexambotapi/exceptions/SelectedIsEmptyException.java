package ru.qwarn.pddexambotapi.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class SelectedIsEmptyException extends TelegramBotCustomException {
    public SelectedIsEmptyException(String message) {
        super(message);
    }
}
