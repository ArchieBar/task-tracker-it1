package ru.itone.model.board.invite;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import ru.itone.model.board.Board;
import ru.itone.model.user.User;

import javax.persistence.*;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "Invitations")
public class Invite {
    @Id
    @Type(type = "org.hibernate.type.UUIDCharType")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "board_id")
    private Board board;

    @Column(name = "confirmed")
    private Boolean confirmed;

    public Invite(User user, Board board) {
        this.user = user;
        this.board = board;
        this.confirmed = false;
    }
}
