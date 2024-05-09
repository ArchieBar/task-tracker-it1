package ru.itone.exception.epic;

import java.util.UUID;

public class EpicByIdNotFoundException extends RuntimeException {
    public EpicByIdNotFoundException(UUID uuid) {
        super(String.format("Эпик с ID: '%s' не найден.", uuid));
    }
}
