package ru.qwarn.pddexambotapi.aspects;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import ru.qwarn.pddexambotapi.exceptions.*;

@Slf4j
@Aspect
@Component
public class ServiceAspect {

    @Pointcut("within(ru.qwarn.pddexambotapi.services.*)")
    public void serviceLayer(){}

    @Before(value = "serviceLayer()")
    public void logBefore(JoinPoint joinPoint){
        log.info("Starting execution of method {} with args [{}]",
                joinPoint.getSignature().getName(), joinPoint.getArgs());
    }

    @AfterThrowing(value = "serviceLayer()", throwing = "exception")
    public void logCustomExceptions(JoinPoint joinPoint, TelegramBotCustomException exception){
        log.warn("Method {} threw client exception {} with message {}",
                joinPoint.getSignature().getName(), exception.getClass().getName(), exception.getMessage());
    }

    @AfterReturning(pointcut = "serviceLayer()", returning = "res")
    public void logAfterReturning(JoinPoint joinPoint, Object res){
        log.info("Got result [{}] from method {}",
                res, joinPoint.getSignature().getName());
    }

    @After(value = "serviceLayer()")
    public void logAfter(JoinPoint joinPoint){
        log.info("Ended execution of method {}", joinPoint.getSignature().getName());
    }

}
