package ru.qwarn.pddexambotapi.exceptions;

public class ListIsEmptyException extends RuntimeException{
    public ListIsEmptyException(String message) {
        super(message);
    }
}
