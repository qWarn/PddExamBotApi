package ru.qwarn.pddexambotapi.exceptions;

public class UserDoesntExistsException extends RuntimeException{

    public UserDoesntExistsException() {
        super("Пожалуйста, введите команду /start");
    }

}
