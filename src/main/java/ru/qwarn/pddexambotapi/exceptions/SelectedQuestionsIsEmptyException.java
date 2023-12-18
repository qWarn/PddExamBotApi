package ru.qwarn.pddexambotapi.exceptions;

public class SelectedQuestionsIsEmptyException extends RuntimeException{
    public SelectedQuestionsIsEmptyException(String message) {
        super(message);
    }
}
