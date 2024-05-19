package ru.itone.exception.user;

import java.util.UUID;

public class InviteByUserIdAndBoardIdNotFoundException extends RuntimeException {
    public InviteByUserIdAndBoardIdNotFoundException(UUID userId, UUID boardId) {
        super(String.format("Приглашение для пользователя с ID: '%s' в доску с ID: '%s' не найдено.", userId, boardId));
    }
}
