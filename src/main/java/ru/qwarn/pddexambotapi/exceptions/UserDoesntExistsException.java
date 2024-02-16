package ru.qwarn.pddexambotapi.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UserDoesntExistsException extends TelegramBotCustomException{

    public UserDoesntExistsException() {
        super("Пожалуйста, введите команду /start");
    }

}
