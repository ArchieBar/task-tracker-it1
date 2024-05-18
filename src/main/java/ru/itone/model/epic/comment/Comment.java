package ru.itone.model.epic.comment;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Type;
import ru.itone.model.epic.Epic;
import ru.itone.model.epic.comment.dto.CommentDto;
import ru.itone.model.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
@Table(name = "Comments")
public class Comment {
    @Id
    @Type(type = "org.hibernate.type.UUIDCharType")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "text")
    private String text;

    @Column(name = "created_time")
    private LocalDateTime createdTime;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "author_id")
    private User author;

    @ManyToOne
    @JoinColumn(name = "epic_id")
    private Epic epic;

    public Comment(CommentDto dto, User author, Epic epic) {
        this.text = dto.getText();
        this.createdTime = LocalDateTime.now();
        this.author = author;
        this.epic = epic;
    }
}
