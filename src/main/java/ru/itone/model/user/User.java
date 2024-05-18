package ru.itone.model.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import ru.itone.model.epic.Epic;
import ru.itone.model.user.dto.RegisterFormDto;
import ru.itone.model.user.dto.UserDto;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
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

    @Column(name = "password")
    private String password;

    @Column(name = "logon")
    private Boolean logon;

    @OneToMany
    @JoinColumn(name = "user_id")
    private Set<Entitlement> entitlements;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "Epics_Users",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "epic_id", referencedColumnName = "id")
    )
    private Set<Epic> epics;

    public User(UserDto userDto) {
        this.firstName = userDto.getFirstName().substring(0, 1).toUpperCase() +
                userDto.getFirstName().substring(1).toLowerCase();

        this.lastName = userDto.getLastName().substring(0, 1).toUpperCase() +
                userDto.getLastName().substring(1).toLowerCase();

        this.email = userDto.getEmail();
        this.entitlements = new HashSet<>();
        this.epics = new HashSet<>();
    }

    public User(RegisterFormDto dto) {
        this.firstName = dto.getFirstName().substring(0, 1).toUpperCase() +
                dto.getFirstName().substring(1).toLowerCase();

        this.lastName = dto.getLastName().substring(0, 1).toUpperCase() +
                dto.getLastName().substring(1).toLowerCase();

        this.email = dto.getEmail();
        this.password = dto.getPassword();
        this.logon = true;
        this.entitlements = new HashSet<>();
        this.epics = new HashSet<>();
    }

    public void addEntitlement(Entitlement entitlement) {
        if (entitlements != null) {
            entitlements.add(entitlement);
        } else {
            this.entitlements = new HashSet<>();
            entitlements.add(entitlement);
        }
    }
}
