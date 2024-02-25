package ru.qwarn.pddexambotapi.exceptions;

public class TelegramBotCustomException extends RuntimeException {
    public TelegramBotCustomException(String message) {
        super(message);
    }

}
