package ru.qwarn.PddExamBotApi.exceptions;

public class InvalidAnswerException extends RuntimeException{
    public InvalidAnswerException(String message) {
        super(message);
    }
}
