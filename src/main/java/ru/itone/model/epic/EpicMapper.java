package ru.itone.model.epic;

import ru.itone.model.epic.comment.CommentMapper;
import ru.itone.model.epic.comment.dto.CommentResponseDto;
import ru.itone.model.epic.dto.EpicResponseDto;
import ru.itone.model.task.TaskMapper;
import ru.itone.model.task.dto.TaskResponseDto;
import ru.itone.model.user.UserMapper;
import ru.itone.model.user.dto.UserResponseDto;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class EpicMapper {
    public static EpicResponseDto toEpicResponseDto(Epic epic) {
        EpicResponseDto epicResponseDto = new EpicResponseDto();

        epicResponseDto.setId(epic.getId());

        epicResponseDto.setName(epic.getName());

        if (epic.getDescription() != null) {
            epicResponseDto.setDescription(epic.getDescription());
        }

        epicResponseDto.setStatus(epic.getStatus().name());

        epicResponseDto.setCreatedTime(epic.getCreatedTime());

        epicResponseDto.setEndTime(epic.getEndTime());


        if (epic.getTasks() != null) {
            List<TaskResponseDto> tasks = TaskMapper.toTaskResponseDtoList(epic.getTasks());
            epicResponseDto.setTasks(tasks);
        }

        if (epic.getActivity() != null) {
            List<CommentResponseDto> activity = CommentMapper.toCommentResponseDtoList(epic.getActivity());
            epicResponseDto.setActivity(activity);
        }

        if (epic.getUsers() != null) {
            List<UserResponseDto> users = UserMapper.toUserResponseDtoList(epic.getUsers());
            epicResponseDto.setUsers(users);
        }

        return epicResponseDto;
    }

    public static List<EpicResponseDto> toEpicResponseDtoList(Set<Epic> epics) {
        return epics.stream()
                .map(EpicMapper::toEpicResponseDto)
                .collect(Collectors.toList());
    }
}
