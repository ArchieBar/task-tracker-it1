package ru.itone.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.itone.exception.epic.comment.CommentByIdNotFoundException;
import ru.itone.exception.model.ErrorResponse;
import ru.itone.exception.epic.EpicByIdNotFoundException;
import ru.itone.exception.task.TaskByIdNotFoundException;
import ru.itone.exception.user.UserAccessDeniedException;
import ru.itone.exception.user.UserByIdNotFoundException;
import ru.itone.exception.user.UserRightsByUserIdAndBoardIdNotFoundException;

import javax.validation.ValidationException;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler({
            ValidationException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    private ErrorResponse objectValidationException(RuntimeException e) {
        log.info("Ошибка валидации: {}", e.getMessage());
        return new ErrorResponse("Ошибка валидации", e.getMessage());
    }

    @ExceptionHandler({
            UserByIdNotFoundException.class,
            EpicByIdNotFoundException.class,
            CommentByIdNotFoundException.class,
            TaskByIdNotFoundException.class
    })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    private ErrorResponse objectNotFoundByIdHandler(RuntimeException e) {
        log.info("Объект не найден: {}", e.getMessage());
        return new ErrorResponse("Объект не найден", e.getMessage());
    }

    @ExceptionHandler({
            UserAccessDeniedException.class,
            UserRightsByUserIdAndBoardIdNotFoundException.class
    })
    @ResponseStatus(HttpStatus.FORBIDDEN)
    private ErrorResponse accessDeniedHandler(RuntimeException e) {
        log.info("Отказ в доступе: {}", e.getMessage());
        return new ErrorResponse("Отказ в доступе", e.getMessage());
    }

    @ExceptionHandler()
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    private ErrorResponse serverErrorHandler(Throwable exception) {
        log.debug("Ошибка сервера: ", exception);
        return new ErrorResponse("Ошибка сервера", exception.getMessage());
    }
}
