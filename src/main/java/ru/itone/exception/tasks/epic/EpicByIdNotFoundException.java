package ru.itone.exception.tasks.epic;

import java.util.UUID;

public class EpicByIdNotFoundException extends RuntimeException {
    public EpicByIdNotFoundException(UUID uuid) {
        super(String.format("Эпик с ID: '%s' не найден.", uuid));
    }
}
