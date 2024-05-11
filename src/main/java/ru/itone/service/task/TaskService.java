package ru.itone.service.task;

import ru.itone.model.task.dto.TaskDto;
import ru.itone.model.task.dto.TaskResponseDto;

import java.util.List;
import java.util.UUID;

public interface TaskService {
    List<TaskResponseDto> findTasksByEpicId(UUID epicId);

    TaskResponseDto findTaskById(UUID taskId);

    TaskResponseDto createTaskById(UUID epicId, TaskDto taskDto);

    TaskResponseDto updateTaskById(UUID taskId, TaskDto taskDto);

    TaskResponseDto updateCompletedTask(UUID epicId, UUID taskId, Boolean completed);

    void deleteTaskById(UUID epicId, UUID taskId);
}
