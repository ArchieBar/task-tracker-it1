package ru.itone.model.epic.comment.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    @NotBlank(message = "Текст комментария не может быть пустым или состоять только из пробелов.")
    @Size(
            max = 1000,
            message = "Длина текста комментария не может быть больше 1000 символов"
    )
    private String text;
}
