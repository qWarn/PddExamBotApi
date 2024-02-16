package ru.qwarn.pddexambotapi.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class SelectedIsEmptyException extends TelegramBotCustomException{
    public SelectedIsEmptyException(String message) {
        super(message);
    }
}
