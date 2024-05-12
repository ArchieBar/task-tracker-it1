package ru.itone.exception.user;

import java.util.UUID;

public class UserAccessDeniedException extends RuntimeException {
    public UserAccessDeniedException(UUID userId) {
        super(String.format("Пользователю с ID: '%s' отказано в доступе.", userId));
    }
}
