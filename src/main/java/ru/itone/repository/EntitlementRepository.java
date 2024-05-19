package ru.itone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import ru.itone.model.user.Entitlement;
import ru.itone.model.user.EntitlementEnum;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EntitlementRepository extends JpaRepository<Entitlement, UUID> {

    Optional<Entitlement> findByUserIdAndBoardId(UUID userId, UUID boardId);

    List<Entitlement> findAllByBoardId(UUID boardId);

    List<Entitlement> findAllByUserIdAndEntitlement(UUID userId, EntitlementEnum entitlement);

    @Modifying
    @Transactional
    @Query("DELETE FROM Entitlement e WHERE e.board.id = :id")
    void deleteAllByBoardId(UUID id);

    @Modifying
    @Transactional
    void deleteAllByUserId(UUID id);
}
