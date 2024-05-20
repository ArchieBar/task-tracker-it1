package ru.itone.exception.user;

import java.util.UUID;

public class UserNotFoundInEpicException extends RuntimeException {
    public UserNotFoundInEpicException(UUID epicId, UUID userId) {
        super(String.format("Пользователь в эпике с ID: '%s' не найден пользователь с ID: '%s'", epicId, userId));
    }
}
