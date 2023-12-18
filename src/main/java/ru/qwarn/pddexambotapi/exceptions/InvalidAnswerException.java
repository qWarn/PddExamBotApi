package ru.qwarn.pddexambotapi.exceptions;

public class InvalidAnswerException extends RuntimeException{
    public InvalidAnswerException(String message) {
        super(message);
    }
}
