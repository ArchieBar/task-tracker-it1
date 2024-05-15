package ru.itone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import ru.itone.model.user.Entitlement;

import java.util.Optional;
import java.util.UUID;

public interface EntitlementRepository extends JpaRepository<Entitlement, UUID> {

    Optional<Entitlement> findByUserIdAndBoardId(UUID userId, UUID boardId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Entitlement e WHERE e.board.id = :id")
    void deleteAllByBoardId(UUID id);
}
