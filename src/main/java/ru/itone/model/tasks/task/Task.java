package ru.itone.model.tasks.task;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import ru.itone.model.tasks.task.dto.TaskDto;

import javax.persistence.*;
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

    @Column(name = "description")
    private String description;

    @Column(name = "is_completed")
    private Boolean isCompleted;

    //TODO
    // Проверить, может ли случиться NPE
    public Task(TaskDto taskDto) {
        this.description = taskDto.getDescription();
        this.isCompleted = false;
    }
}
