package ru.itone.model.epic.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.itone.model.Marker;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EpicDto {
    @NotNull(
            groups = Marker.toCreate.class,
            message = "Название не может пыть пустым или состоять только из пробелов."
    )
    @Pattern(
            regexp = "^(\\s*[A-Za-zА-ЯЁёа-я 0-9-]+\\s*)*$",
            message = "Название может содержать только латинские / кириллические символы, цифры, знаки пробела и дефис."
    )
    @Size(
            max = 255,
            message = "Название не может быть больше 255 символов."
    )
    private String name;

    @NotNull(
            groups = Marker.toCreate.class,
            message = "Описание не может пыть пустым или состоять только из пробелов."
    )
    @Pattern(
            regexp = "^(\\s*[A-Za-zА-ЯЁёа-я 0-9-]+\\s*)*$",
            message = "Описание может содержать только латинские / кириллические символы, цифры, знаки пробела и дефис."
    )
    @Size(
            max = 1000,
            message = "Описание эпика не может быть больше 1000 символов."
    )
    private String description;

    @NotNull(
            groups = Marker.toCreate.class,
            message = "Время окончания не может быть null"
    )
    @Future(
            message = "Время окончания задачи не может быть в настоящем или прошлом"
    )
    private LocalDateTime endTime;
}
