package ru.itone.exception.user;

import java.util.UUID;

public class UserRightsByUserIdAndBoardIdNotFoundException extends RuntimeException {
    public UserRightsByUserIdAndBoardIdNotFoundException(UUID userId, UUID boardId) {
        super(String.format("Права пользователя с ID: %s в доске задач с ID: %s не найдены.", userId, boardId));
    }
}
