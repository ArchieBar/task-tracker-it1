package ru.itone.exception.epic.comment;

import java.util.UUID;

public class CommentByIdNotFoundException extends RuntimeException {
    public CommentByIdNotFoundException(UUID commentId) {
        super(String.format("Комментарий с ID: '%s' не найден.", commentId));
    }
}
