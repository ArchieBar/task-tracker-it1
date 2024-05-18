package ru.itone.model.user.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;

@Getter
@Setter
public class RegisterFormDto {
    @NotNull(message = "Имя не может пыть пустым или состоять только из пробелов.")
    @Pattern(
            regexp = "^(\\s*[A-Za-zА-ЯЁёа-я]+\\s*)*$",
            message = "Имя может содержать только латинские или кириллические символы."
    )
    @Size(
            max = 255,
            message = "Имя не может быть больше 255 символов."
    )
    private String firstName;

    @NotNull(message = "Фамилия не может пыть пустым или состоять только из пробелов.")
    @Pattern(
            regexp = "^(\\s*[A-Za-zА-ЯЁёа-я]+\\s*)*$",
            message = "Имя может содержать только латинские или кириллические символы."
    )
    @Size(
            max = 255,
            message = "Фамилия не может быть больше 255 символов."
    )
    private String lastName;

    @NotNull(message = "Почтовый адрес не может быть пустым.")
    @Size(
            max = 255,
            message = "Почтовый адрес не может быть больше 255 символов."
    )
    @Email(message = "Почтовый адрес должен быть в формате: 'email@email.email'.")
    private String email;

    @NotBlank(message = "Пароль не может быть пустым или состоять только из пробелов.")
    @Size(
            min = 5,
            message = "Пароль должен содержать минимум 5 символов. "
    )
    private String password;
}
