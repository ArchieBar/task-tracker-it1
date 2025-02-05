package ru.itone.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.itone.model.Marker;
import ru.itone.model.board.dto.BoardDto;
import ru.itone.model.board.dto.BoardResponseDto;
import ru.itone.model.user.EntitlementEnum;
import ru.itone.service.board.BoardService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import javax.websocket.server.PathParam;
import java.util.List;
import java.util.UUID;

@Slf4j
@Validated
@RestController
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController {
    private final BoardService boardService;

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

    @PatchMapping("/{boardId}/invite/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public String inviteUser(@RequestHeader("X-User-Id") UUID owner,
                             @PathVariable UUID boardId,
                             @PathVariable UUID userId) {
        log.info("Вызов PATCH-операции: /board/{boardId}/invite/{userId}");
        boardService.inviteUser(owner, boardId, userId);
        return "Приглашение успешно отправлено.";
    }

    @PatchMapping("/{boardId}/issueLicense/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public String issueEntitlement(@RequestHeader("X-User-Id") UUID ownerId,
                                   @PathVariable UUID boardId,
                                   @PathVariable UUID userId,
                                   @PathParam("entitlement") EntitlementEnum entitlement) {
        log.info("Вызов PATCH-операции: /board/{boardId}/issueLicense/{userId}");
        boardService.issueEntitlement(ownerId, boardId, userId, entitlement);
        return "Права пользователя обновлены.";
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
        boardService.deleteBoardById(userId, boardId);
    }
}
