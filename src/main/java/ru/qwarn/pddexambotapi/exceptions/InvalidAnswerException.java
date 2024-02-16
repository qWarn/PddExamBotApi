package ru.qwarn.pddexambotapi.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class InvalidAnswerException extends TelegramBotCustomException{
    public InvalidAnswerException(String message) {
        super(message);
    }
}
