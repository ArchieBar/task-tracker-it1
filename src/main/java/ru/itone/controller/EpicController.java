package ru.itone.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.itone.model.Marker;
import ru.itone.model.epic.comment.dto.CommentDto;
import ru.itone.model.epic.comment.dto.CommentResponseDto;
import ru.itone.model.epic.dto.EpicDto;
import ru.itone.model.epic.dto.EpicResponseDto;
import ru.itone.service.epic.EpicService;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@Slf4j
@Validated
@RestController
@RequestMapping("/epic")
public class EpicController {
    private final EpicService epicService;

    @Autowired
    public EpicController(EpicService epicService) {
        this.epicService = epicService;
    }

    @GetMapping("/all/{boardId}")
    @ResponseStatus(HttpStatus.OK)
    public List<EpicResponseDto> findEpicsByBoardId(@PathVariable UUID boardId) {
        log.info("Вызов GET-операции: /epic/all/{boardId}");
        return epicService.findEpicsByBoardId(boardId);
    }

    @GetMapping("/{epicId}")
    @ResponseStatus(HttpStatus.OK)
    public EpicResponseDto findEpicById(@PathVariable UUID epicId) {
        log.info("Вызов GET-операции: /epic/{epicId}");
        return epicService.findEpicById(epicId);
    }

    @PostMapping("/{boardId}")
    @ResponseStatus(HttpStatus.CREATED)
    @Validated({Marker.toCreate.class})
    public EpicResponseDto createEpic(@RequestHeader("X-User-Id") UUID userId,
                                      @PathVariable UUID boardId,
                                      @RequestBody @Valid EpicDto epicDto) {
        log.info("Вызов POST-операции: /epic/{boardId}");
        return epicService.createEpic(userId, boardId, epicDto);
    }

    @PostMapping("/{epicId}/comment")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponseDto createdCommentByEpicId(@RequestHeader("X-User-Id") UUID userId,
                                                     @PathVariable UUID epicId,
                                                     @RequestBody @Valid CommentDto commentDto) {
        log.info("Вызов POST-операции: /epic/{epicId}/comment");
        return epicService.createCommentByEpicId(userId, epicId, commentDto);
    }

    @PatchMapping("/{epicId}")
    @ResponseStatus(HttpStatus.OK)
    @Validated({Marker.toUpdate.class})
    public EpicResponseDto updateEpicById(@RequestHeader("X-User-Id") UUID userId,
                                          @PathVariable UUID epicId,
                                          @RequestBody @Valid EpicDto epicDto) {
        log.info("Вызов PATCH-операции: /epic/{epicId}");
        return epicService.updateEpicById(userId, epicId, epicDto);
    }

    @PatchMapping("/comment/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentResponseDto updateCommentById(@RequestHeader("X-User-Id") UUID userId,
                                                @PathVariable UUID commentId,
                                                @RequestBody @Valid CommentDto commentDto) {
        log.info("Вызов PATCH-операции: /epic/comment/{commentId}");
        return epicService.updateCommentById(userId, commentId, commentDto);
    }

    @DeleteMapping("/{boardId}/{epicId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEpicById(@RequestHeader("X-User-Id") UUID userId,
                               @PathVariable UUID boardId,
                               @PathVariable UUID epicId) {
        log.info("Вызов DELETE-операции: /epic/{boardId}/{epicId}");
        epicService.deleteEpicById(userId, boardId, epicId);
    }

    @DeleteMapping("{epicId}/comment/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommentById(@RequestHeader("X-User-Id") UUID userId,
                                  @PathVariable UUID epicId,
                                  @PathVariable UUID commentId) {
        log.info("Вызов DELETE-операции /epic/{epicId}/comment/{commentId}");
        epicService.deleteCommentById(userId, epicId, commentId);
    }
}
