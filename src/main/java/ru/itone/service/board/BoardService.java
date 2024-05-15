package ru.itone.service.board;

import org.springframework.data.domain.Pageable;
import ru.itone.model.board.dto.BoardDto;
import ru.itone.model.board.dto.BoardResponseDto;

import java.util.List;
import java.util.UUID;

public interface BoardService {
    List<BoardResponseDto> findBoards(Pageable pageable);

    BoardResponseDto findBoardById(UUID boardId);

    BoardResponseDto createBoard(UUID userId, BoardDto boardDto);

    BoardResponseDto updateBoardById(UUID userId, UUID boardId, BoardDto boardDto);

    void deleteBoardById(UUID userId, UUID boardId);
}
