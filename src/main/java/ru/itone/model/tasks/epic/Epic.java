package ru.itone.model.tasks.epic;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.itone.model.tasks.task.Task;
import ru.itone.model.user.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@Entity
@Table(name = "Epics")
public class Epic {
    @Id
    @NotNull
    private UUID id;

    @NotBlank(message = "Название не может пыть пустым или состоять только из пробелов.")
    @Size(max = 255, message = "Название не может быть больше 255 символов.")
    @Column(name = "name")
    private String name;

    @NotBlank(message = "Описание задачи не может быть пустым или состоять только из пробелов.")
    @Column(name = "description")
    private String description;

    @OneToMany(fetch = FetchType.EAGER)
    private Set<Task> tasks;

    @ManyToMany(mappedBy = "epics", fetch = FetchType.LAZY)
    private Set<User> users;
}
