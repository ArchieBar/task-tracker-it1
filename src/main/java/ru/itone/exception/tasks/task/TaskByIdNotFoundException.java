package ru.itone.exception.tasks.task;

import java.util.UUID;

public class TaskByIdNotFoundException extends RuntimeException {
    public TaskByIdNotFoundException(UUID taskId) {
        super(String.format("Задача с ID: '%s' не найдена.", taskId));
    }
}
