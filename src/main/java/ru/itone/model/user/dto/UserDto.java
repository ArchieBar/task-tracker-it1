package ru.itone.model.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import ru.itone.model.Marker;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
public class UserDto {
    @NotNull(
            groups = Marker.toCreate.class,
            message = "Имя не может пыть пустым или состоять только из пробелов."
    )
    @Pattern(
            regexp = "^(\\s*[A-Za-zА-ЯЁёа-я]+\\s*)*$",
            message = "Имя может содержать только латинские или кириллические символы."
    )
    @Size(
            max = 255,
            message = "Имя не может быть больше 255 символов."
    )
    private String firstName;

    @NotNull(
            groups = Marker.toCreate.class,
            message = "Фамилия не может пыть пустым или состоять только из пробелов."
    )
    @Pattern(
            regexp = "^(\\s*[A-Za-zА-ЯЁёа-я]+\\s*)*$",
            message = "Имя может содержать только латинские или кириллические символы."
    )
    @Size(
            max = 255,
            message = "Фамилия не может быть больше 255 символов."
    )
    private String lastName;

    @NotNull(
            groups = Marker.toCreate.class,
            message = "Почтовый адрес не может быть пустым."
    )
    @Size(
            max = 255,
            message = "Почтовый адрес не может быть больше 255 символов."
    )
    @Email(
            message = "Почтовый адрес должен быть в формате: 'email@email.email'."
    )
    private String email;
}
