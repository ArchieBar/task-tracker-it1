package ru.itone.model.task;

import ru.itone.model.task.dto.TaskResponseDto;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TaskMapper {
    //TODO
    // Пробросить NPE?
    public static TaskResponseDto toTaskResponseDto(Task task) {
        TaskResponseDto taskResponseDto = new TaskResponseDto();

        if (task.getId() != null) {
            taskResponseDto.setId(task.getId());
        }

        if (task.getDescription() != null) {
            taskResponseDto.setDescription(task.getDescription());
        }

        if (task.getIsCompleted() != null) {
            taskResponseDto.setIsCompleted(task.getIsCompleted());
        }

        return taskResponseDto;
    }

    public static List<TaskResponseDto> toTaskResponseDtoList(Set<Task> tasks) {
        return tasks.stream()
                .map(TaskMapper::toTaskResponseDto)
                .collect(Collectors.toList());
    }
}
