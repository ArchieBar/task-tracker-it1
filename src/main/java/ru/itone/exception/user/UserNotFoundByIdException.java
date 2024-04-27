package ru.itone.exception.user;

import java.text.MessageFormat;
import java.util.UUID;

public class UserNotFoundByIdException extends RuntimeException {

    public UserNotFoundByIdException(String message) {
        super(message);
    }

    public UserNotFoundByIdException(UUID userId) {
        super(
                MessageFormat.format("Пользователь с ID: {0} не найден.", userId)
        );
    }
}
