package ru.itone.exception.user;

import java.util.UUID;

public class EntitlementByUserIdAndBoardIdNotFoundException extends RuntimeException {
    public EntitlementByUserIdAndBoardIdNotFoundException(UUID userId, UUID boardId) {
        super(String.format("Права пользователя с ID: '%s' в доске с ID: '%s' не найдены.", userId, boardId));
    }
}
