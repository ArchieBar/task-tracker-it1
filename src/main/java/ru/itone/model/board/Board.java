package ru.itone.model.board;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import ru.itone.model.board.dto.BoardDto;
import ru.itone.model.epic.Epic;
import ru.itone.model.user.User;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@Entity
@Table(name = "Boards")
public class Board {
    @Id
    @Type(type = "org.hibernate.type.UUIDCharType")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "name")
    private String name;

    @OneToMany
    @JoinTable(
            name = "Boards_Epics",
            joinColumns = @JoinColumn(name = "board_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "epic_id", referencedColumnName = "id")
    )
    private List<Epic> epics;

    @ManyToMany
    @JoinTable(
            name = "Boards_Users",
            joinColumns = @JoinColumn(name = "board_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id")
    )
    private List<User> users;

    public Board(BoardDto dto) {
        this.name = dto.getName();
        this.epics = new ArrayList<>();
        this.users = new ArrayList<>();
    }

    //TODO
    // Пробросить NPE
    public void addEpic(Epic epic) {
        if (epics != null) {
            epics.add(epic);
        }
    }

    //TODO
    // Пробросить NPE
    public void addUser(User user) {
        if (users != null) {
            users.add(user);
        }
    }
}
