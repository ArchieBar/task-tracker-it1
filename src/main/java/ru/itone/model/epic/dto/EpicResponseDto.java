package ru.itone.model.epic.dto;

import lombok.*;
import ru.itone.model.epic.comment.dto.CommentResponseDto;
import ru.itone.model.task.dto.TaskResponseDto;
import ru.itone.model.user.dto.UserFullNameAndEmailDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EpicResponseDto {
    private UUID id;
    private String name;
    private String description;
    private String status;
    private LocalDateTime createdTime;
    private LocalDateTime endTime;
    private List<TaskResponseDto> tasks;
    private List<CommentResponseDto> activity;
    private List<UserFullNameAndEmailDto> users;
}
