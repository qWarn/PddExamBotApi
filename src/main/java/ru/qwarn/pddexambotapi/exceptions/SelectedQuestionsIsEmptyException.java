package ru.qwarn.pddexambotapi.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// TOOD: everywhere
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class SelectedQuestionsIsEmptyException extends RuntimeException{
    public SelectedQuestionsIsEmptyException(String message) {
        super(message);
    }
}
