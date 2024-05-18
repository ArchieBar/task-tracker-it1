package ru.itone.model.task;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import ru.itone.model.epic.Epic;
import ru.itone.model.task.dto.TaskDto;

import javax.persistence.*;
import java.util.UUID;

@Getter
@Setter
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "epic_id", updatable = false)
    private Epic epic;

    //TODO
    // Проверить, может ли случиться NPE
    public Task(TaskDto taskDto, Epic epic) {
        this.description = taskDto.getDescription();
        this.isCompleted = false;
        this.epic = epic;
    }
}
