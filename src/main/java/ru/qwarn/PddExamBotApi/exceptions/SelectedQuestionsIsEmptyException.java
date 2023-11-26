package ru.qwarn.PddExamBotApi.exceptions;

public class SelectedQuestionsIsEmptyException extends RuntimeException{
    public SelectedQuestionsIsEmptyException(String message) {
        super(message);
    }
}
