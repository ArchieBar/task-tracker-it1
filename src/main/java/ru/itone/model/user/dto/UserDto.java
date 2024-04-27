package ru.itone.model.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.itone.model.Marker;

import javax.validation.constraints.*;

@Data
@AllArgsConstructor
public class UserDto {
    @NotBlank(groups = {Marker.toCreate.class},
            message = "Имя не может пыть пустым или состоять только из пробелов.")
    @Pattern(groups = {Marker.toCreate.class, Marker.toUpdate.class},
            regexp = "^[A-Za-zА-ЯЁёа-я]*$",
            message = "Имя может содержать только латинские или кириллические символы.")
    @Size(groups = {Marker.toCreate.class, Marker.toUpdate.class},
            max = 255,
            message = "Имя не может быть больше 255 символов.")
    private String firstName;

    @NotBlank(groups = {Marker.toCreate.class},
            message = "Фамилия не может пыть пустым или состоять только из пробелов.")
    @Pattern(groups = {Marker.toCreate.class, Marker.toUpdate.class},
            regexp = "^[A-Za-zА-ЯЁёа-я]*$",
            message = "Имя может содержать только латинские или кириллические символы.")
    @Size(groups = {Marker.toCreate.class, Marker.toUpdate.class},
            max = 255,
            message = "Фамилия не может быть больше 255 символов.")
    private String lastName;

    @NotNull(groups = {Marker.toCreate.class},
            message = "Почтовый адрес не может быть пустым.")
    @Size(groups = {Marker.toCreate.class, Marker.toUpdate.class},
            max = 255,
            message = "Почтовый адрес не может быть больше 255 символов.")
    @Email(groups = {Marker.toCreate.class, Marker.toUpdate.class},
            message = "Почтовый адрес должен быть в формате: 'email@email.email'.")
    private String email;
}
