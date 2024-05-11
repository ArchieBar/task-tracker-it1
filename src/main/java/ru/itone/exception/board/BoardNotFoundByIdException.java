package ru.itone.exception.board;

import java.util.UUID;

public class BoardNotFoundByIdException extends RuntimeException {
    public BoardNotFoundByIdException(UUID boardId) {
        super(String.format("Доска задач с ID: %s не найдена.", boardId));
    }
}
