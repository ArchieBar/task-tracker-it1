package ru.itone.model.epic;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import ru.itone.model.epic.dto.EpicDto;
import ru.itone.model.task.Task;
import ru.itone.model.user.User;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@Entity
@Table(name = "Epics")
public class Epic {
    @Id
    @Type(type = "org.hibernate.type.UUIDCharType")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Enumerated
    @Column(name = "status")
    private EpicStatus status;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "Epics_Tasks",
            joinColumns = @JoinColumn(name = "epic_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "task_id", referencedColumnName = "id")
    )
    private List<Task> tasks;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "Epics_Users",
            joinColumns = @JoinColumn(name = "epic_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id")
    )
    private List<User> users;

    //TODO
    // Проверить, может ли случиться NPE
    public Epic(EpicDto epicDto) {
        this.name = epicDto.getName();
        this.description = epicDto.getDescription();
        this.status = EpicStatus.TODO;
        this.tasks = new ArrayList<>();
        this.users = new ArrayList<>();
    }

    //TODO
    // Пробросить исключение NPE
    public void addTask(Task task) {
        if (tasks != null) {
            tasks.add(task);
        }
    }

    //TODO
    // Пробросить исключение NPE
    public void addUser(User user) {
        if (users != null) {
            users.add(user);
        }
    }
}
