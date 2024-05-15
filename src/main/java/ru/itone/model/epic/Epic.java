package ru.itone.model.epic;

import lombok.*;
import org.hibernate.annotations.Type;
import ru.itone.model.board.Board;
import ru.itone.model.epic.comment.Comment;
import ru.itone.model.epic.comment.CommentMapper;
import ru.itone.model.epic.dto.EpicDto;
import ru.itone.model.task.Task;
import ru.itone.model.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@Getter
@Setter
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

    @EqualsAndHashCode.Exclude
    @Column(name = "description")
    private String description;

    @Enumerated
    @Column(name = "status")
    private EpicStatus status;

    @Column(name = "created_time")
    private LocalDateTime createdTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "board_id")
    private Board board;

    @OneToOne(fetch =  FetchType.EAGER)
    @JoinColumn(name = "author_id")
    private User author;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "epic_id", updatable = false)
    private Set<Task> tasks;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "epic_id", updatable = false)
    private List<Comment> activity;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "Epics_Users",
            joinColumns = @JoinColumn(name = "epic_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id")
    )
    private Set<User> users;

    //TODO
    // Проверить, может ли случиться NPE
    public Epic(EpicDto dto, Board board, User author) {
        this.name = dto.getName();
        this.description = dto.getDescription();
        this.status = EpicStatus.TODO;
        this.createdTime = LocalDateTime.now();
        this.endTime = dto.getEndTime();
        this.board = board;
        this.author = author;
        this.tasks = new HashSet<>();
        this.activity = new ArrayList<>();
        this.users = new HashSet<>();
    }

    public void addTask(Task task) {
        if (tasks != null) {
            tasks.add(task);
        } else {
            this.tasks = new HashSet<>();
            tasks.add(task);
        }
    }

    public void addComment(Comment comment) {
        if (activity != null) {
            activity.add(comment);
        } else {
            this.activity = new ArrayList<>();
            activity.add(comment);
        }
    }

    public void addUser(User user) {
        if (users != null) {
            users.add(user);
        } else {
            this.users = new HashSet<>();
            users.add(user);
        }
    }
}
