package ru.itone.model.epic.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.itone.model.Marker;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EpicDto {
    @NotBlank(
            groups = {Marker.toCreate.class},
            message = "Название не может пыть пустым или состоять только из пробелов."
    )
    @Pattern(
            groups = {Marker.toCreate.class, Marker.toUpdate.class},
            regexp = "^[A-Za-zА-ЯЁёа-я 0-9-]*$",
            message = "Название может содержать только латинские или кириллические символы."
    )
    @Size(
            max = 255,
            message = "Название не может быть больше 255 символов."
    )
    private String name;

    @NotBlank(
            groups = {Marker.toCreate.class},
            message = "Описание не может пыть пустым или состоять только из пробелов."
    )
    @Pattern(
            groups = {Marker.toCreate.class, Marker.toUpdate.class},
            regexp = "^[A-Za-zА-ЯЁёа-я 0-9-]*$",
            message = "Описание может содержать только латинские или кириллические символы."
    )
    private String description;
}
