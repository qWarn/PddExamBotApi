package ru.qwarn.pddexambotapi.routes;

import lombok.RequiredArgsConstructor;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.springframework.stereotype.Component;
import ru.qwarn.pddexambotapi.exceptions.ListIsEmptyException;
import ru.qwarn.pddexambotapi.services.UserService;


@Component
@RequiredArgsConstructor
public class NotificationRoute extends RouteBuilder {

    private final UserService userService;

    @Override
    public void configure() throws Exception {
        from("timer:NotificationTimer?delay=1000&period=60000")
                .onException(ListIsEmptyException.class)
                    .log(LoggingLevel.WARN, "${exception.message}")
                    .handled(true)
                .end()
                .routeId("Rabbitmq producer route")
                .log(LoggingLevel.INFO, "Started execution of rabbitmq producer route")
                .process(exchange ->
                        exchange.getMessage().setBody(
                                userService.getUsersWithLastActiveMoreThanTwoDays())
                )
                .marshal(new JacksonDataFormat(String.class))
                .to("spring-rabbitmq:pdd_exam_exchange?routingKey=notification_key")
                .log(LoggingLevel.INFO, "Sent to exchange ${body}");
    }
}
