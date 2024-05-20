package ru.itone.model.task.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponseDto {
    private UUID id;
    private String description;
    private Boolean isCompleted;
}
