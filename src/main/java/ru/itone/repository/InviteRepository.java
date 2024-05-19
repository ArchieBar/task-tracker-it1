package ru.itone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itone.model.board.invite.Invite;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InviteRepository extends JpaRepository<Invite, UUID> {
    List<Invite> findAllByUserIdAndConfirmed(UUID userId, Boolean confirmed);

    List<Invite> findAllByBoardId(UUID boardId);

    Optional<Invite> findByUserIdAndBoardId(UUID userId, UUID boardId);
}
