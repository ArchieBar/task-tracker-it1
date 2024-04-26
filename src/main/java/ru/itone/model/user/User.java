package ru.itone.model.user;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.itone.model.tasks.epic.Epic;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@Entity
@Table(name = "Users")
public class User {
    @Id
    @NotNull
    private UUID id;

    @NotBlank(message = "Имя не может пыть пустым или состоять только из пробелов.")
    @Size(max = 255, message = "Имя не может быть больше 255 символов.")
    @Column(name = "first_name")
    private String firstName;

    @NotBlank(message = "Фамилия не может пыть пустым или состоять только из пробелов.")
    @Size(max = 255, message = "Фамилия не может быть больше 255 символов.")
    @Column(name = "last_name")
    private String lastName;

    @NotNull(message = "Почтовый адрес не может быть пустым.")
    @Size(max = 255, message = "Почтовый адрес не может быть больше 255 символов.")
    @Email(message = "Почтовый адрес должен быть в формате: 'email@email.email'.")
    private String email;

    @ManyToMany(fetch = FetchType.LAZY)
    private Set<Epic> epics;

    public User(String firstName, String lastName, String email) {
        this.id = UUID.randomUUID();
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }
}
