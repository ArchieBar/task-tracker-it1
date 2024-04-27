package ru.itone.model.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import ru.itone.model.tasks.epic.Epic;
import ru.itone.model.user.dto.UserDto;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Type(type = "org.hibernate.type.UUIDCharType")
    @Column(name = "id")
    private UUID id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "email")
    private String email;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "Epics_Users",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "epic_id", referencedColumnName = "id"))
    private Set<Epic> epics;

    public User(UserDto userDto) {
        this.firstName = userDto.getFirstName().substring(0, 1).toUpperCase() +
                userDto.getFirstName().substring(1).toLowerCase();
        this.lastName = userDto.getLastName().substring(0, 1).toUpperCase() +
                userDto.getLastName().substring(1).toLowerCase();
        this.email = userDto.getEmail();
        this.epics = new HashSet<>();
    }
}
