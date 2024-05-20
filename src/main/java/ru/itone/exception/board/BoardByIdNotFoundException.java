package ru.itone.exception.board;

import java.util.UUID;

public class BoardByIdNotFoundException extends RuntimeException {
    public BoardByIdNotFoundException(UUID boardId) {
        super(String.format("Доска задач с ID: %s не найдена.", boardId));
    }
}
