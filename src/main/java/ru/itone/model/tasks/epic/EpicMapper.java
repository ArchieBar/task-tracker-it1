package ru.itone.model.tasks.epic;

import ru.itone.model.tasks.epic.dto.EpicResponseDto;
import ru.itone.model.tasks.task.TaskMapper;
import ru.itone.model.tasks.task.dto.TaskResponseDto;
import ru.itone.model.user.UserMapper;
import ru.itone.model.user.dto.UserResponseDto;

import java.util.List;
import java.util.stream.Collectors;

public class EpicMapper {
    //TODO
    // Добавить проброс исключения в случае NPE у полей через блок else
    public static EpicResponseDto toEpicResponseDto(Epic epic) {
        EpicResponseDto epicResponseDto = new EpicResponseDto();

        if (epic.getId() != null) {
            epicResponseDto.setId(epic.getId());
        }

        if (epic.getName() != null) {
            epicResponseDto.setName(epic.getName());
        }

        if (epic.getDescription() != null) {
            epicResponseDto.setDescription(epic.getDescription());
        }

        if (epic.getTasks() != null) {
            List<TaskResponseDto> tasks = TaskMapper.toTaskResponseDtoList(epic.getTasks());
            epicResponseDto.setTasks(tasks);
        }

        if (epic.getUsers() != null) {
            List<UserResponseDto> users = UserMapper.toUserResponseDtoList(epic.getUsers());
            epicResponseDto.setUsers(users);
        }

        return epicResponseDto;
    }

    public static List<EpicResponseDto> toEpicResponseDtoList(List<Epic> epics) {
        return epics.stream()
                .map(EpicMapper::toEpicResponseDto)
                .collect(Collectors.toList());
    }
}
