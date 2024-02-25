package ru.qwarn.pddexambotapi.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CallbackConstants {
    public static final String GET_TICKETS = "get_tickets";
    public static final String GET_MORE_TICKETS = "get_more_tickets";
    public static final String START_SELECTED = "start_selected";
    public static final String START_TICKET = "start_ticket";
    public static final String GET_QUESTION = "get_question";
    public static final String ADD_TO_SELECTED = "add_to_selected";
    public static final String REMOVE_FROM_SELECTED = "remove_from_selected";
    public static final String GET_ANSWER = "get_answer";

}
