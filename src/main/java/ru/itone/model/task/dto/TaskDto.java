package ru.itone.model.task.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.itone.model.Marker;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskDto {
    @NotNull(
            groups = Marker.toCreate.class,
            message = "Описание задачи не может быть пустым или состоять только из пробелов."
    )
    @Pattern(
            regexp = "^(\\s*[A-Za-zА-ЯЁёа-я 0-9-]+\\s*)*$",
            message = "Описание задачи может содержать только латинские или кириллические символы."
    )
    private String description;
}
