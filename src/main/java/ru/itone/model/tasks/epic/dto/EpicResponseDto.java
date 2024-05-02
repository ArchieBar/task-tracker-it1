package ru.itone.model.tasks.epic.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.itone.model.tasks.task.dto.TaskResponseDto;
import ru.itone.model.user.dto.UserResponseDto;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EpicResponseDto {
    private UUID id;
    private String name;
    private String description;
    private List<TaskResponseDto> tasks;
    private List<UserResponseDto> users;
}
