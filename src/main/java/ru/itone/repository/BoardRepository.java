package ru.itone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.itone.model.board.Board;

import java.util.UUID;

@Repository
public interface BoardRepository extends JpaRepository<Board, UUID> {
}
