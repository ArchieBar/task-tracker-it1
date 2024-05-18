package ru.itone.model.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import ru.itone.model.board.Board;

import javax.persistence.*;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Entitlements")
public class Entitlement {
    @Id
    @Type(type = "org.hibernate.type.UUIDCharType")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "board_id")
    private Board board;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated
    @Column(name = "entitlement")
    private EntitlementEnum entitlement;

    public Entitlement(Board board, User user, EntitlementEnum entitlement) {
        this.board = board;
        this.user = user;
        this.entitlement = entitlement;
    }
}
