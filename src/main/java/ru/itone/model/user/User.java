package ru.itone.model.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import ru.itone.model.tasks.epic.Epic;
import ru.itone.model.user.dto.UserDto;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Users")
public class User {
    @Id
    @Type(type = "org.hibernate.type.UUIDCharType")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "email")
    private String email;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "Epics_Users",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "epic_id", referencedColumnName = "id")
    )
    private List<Epic> epics;

    //TODO
    // Проверить, может ли случиться NPE
    public User(UserDto userDto) {
        this.firstName = userDto.getFirstName().substring(0, 1).toUpperCase() +
                userDto.getFirstName().substring(1).toLowerCase();

        this.lastName = userDto.getLastName().substring(0, 1).toUpperCase() +
                userDto.getLastName().substring(1).toLowerCase();

        this.email = userDto.getEmail();

        this.epics = new ArrayList<>();
    }

    //TODO
    // Пробросить исключение NPE
    public void addEpic(Epic epic) {
        if (epics != null) {
            epics.add(epic);
        }
    }
}
