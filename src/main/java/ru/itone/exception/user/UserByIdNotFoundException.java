package ru.itone.exception.user;

import java.util.UUID;

public class UserByIdNotFoundException extends RuntimeException {
    public UserByIdNotFoundException(UUID userId) {
        super(String.format("Пользователь с ID: '%s' не найден.", userId));
    }
}
