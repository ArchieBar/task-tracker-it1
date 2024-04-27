package ru.itone.model.tasks.task;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@NoArgsConstructor
@Entity
@Table(name = "Tasks")
public class Task {
    @Id
    @Type(type = "org.hibernate.type.UUIDCharType")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @NotBlank(message = "Описание задачи не может быть пустым или состоять только из пробелов.")
    @Column(name = "description")
    private String description;

    @NotNull(message = "Статус выполненности задачи не может быть пустым.")
    @Column(name = "is_completed")
    private Boolean isCompleted;

    public Task(String description) {
        this.description = description;
        this.isCompleted = false;
    }
}
