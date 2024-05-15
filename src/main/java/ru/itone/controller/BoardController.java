package ru.itone.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.itone.model.Marker;
import ru.itone.model.board.dto.BoardDto;
import ru.itone.model.board.dto.BoardResponseDto;
import ru.itone.service.board.BoardService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;
import java.util.UUID;

@Slf4j
@Validated
@RestController
@RequestMapping("/board")
public class BoardController {
    private final BoardService boardService;

    @Autowired
    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<BoardResponseDto> findBoards(@PositiveOrZero @RequestParam(name = "page", defaultValue = "0") Integer page,
                                             @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Вызов GET-операции: /board");

        Pageable pageable = PageRequest.of(page, size);

        return boardService.findBoards(pageable);
    }

    @GetMapping("/{boardId}")
    @ResponseStatus(HttpStatus.OK)
    public BoardResponseDto findBoardById(@PathVariable UUID boardId) {
        log.info("Вызов GET-операции: /board/{boardId}");
        return boardService.findBoardById(boardId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Validated({Marker.toCreate.class})
    public BoardResponseDto createBoard(@RequestHeader("X-User-Id") UUID userId,
                                        @RequestBody @Valid BoardDto boardDto) {
        log.info("Вызов POST-операции: /board");
        return boardService.createBoard(userId, boardDto);
    }

    @PatchMapping("/{boardId}")
    @ResponseStatus(HttpStatus.OK)
    @Validated({Marker.toUpdate.class})
    public BoardResponseDto updateBoardById(@RequestHeader("X-User-Id") UUID userId,
                                            @PathVariable UUID boardId,
                                            @RequestBody @Valid BoardDto boardDto) {
        log.info("Вызов PATCH-операции: /board/{boardId}");
        return boardService.updateBoardById(userId, boardId, boardDto);
    }

    @DeleteMapping("/{boardId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBoardById(@RequestHeader("X-User-Id") UUID userId,
                                @PathVariable UUID boardId) {
        log.info("Вызов DELETE-операции: /board/{boardId}");
        boardService.deleteBoardById(userId ,boardId);
    }
}
