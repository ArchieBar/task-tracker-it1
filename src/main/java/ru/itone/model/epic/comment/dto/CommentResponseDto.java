package ru.itone.model.epic.comment.dto;

import lombok.*;
import ru.itone.model.user.dto.UserFullNameAndEmailDto;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponseDto {
    private UUID id;
    private String text;
    private LocalDateTime createdTime;
    private UserFullNameAndEmailDto author;
}
