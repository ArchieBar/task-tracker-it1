package ru.itone.service.tasks.task;

import ru.itone.model.tasks.task.dto.TaskDto;
import ru.itone.model.tasks.task.dto.TaskResponseDto;

import java.util.List;
import java.util.UUID;

public interface TaskService {
    List<TaskResponseDto> findTasksByEpicId(UUID epicId);

    TaskResponseDto findTaskById(UUID epicId, UUID taskId);

    TaskResponseDto createTaskById(UUID epicId, TaskDto taskDto);

    TaskResponseDto updateTaskById(UUID epicId, UUID taskId, TaskDto taskDto);

    TaskResponseDto updateCompletedTask(UUID epicId, UUID taskId, Boolean completed);

    void deleteTaskById(UUID epicId, UUID taskId);
}
