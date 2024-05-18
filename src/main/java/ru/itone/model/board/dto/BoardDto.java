package ru.itone.model.board.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.itone.model.Marker;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoardDto {
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
}
