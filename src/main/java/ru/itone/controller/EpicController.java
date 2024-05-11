package ru.itone.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.itone.model.Marker;
import ru.itone.model.epic.dto.EpicDto;
import ru.itone.model.epic.dto.EpicResponseDto;
import ru.itone.service.epic.EpicService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
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
    public EpicResponseDto createEpic(@PathVariable UUID boardId,
                                      @RequestBody @Valid EpicDto epicDto) {
        log.info("Вызов POST-операции: /epic/{boardId}");
        return epicService.createEpic(boardId, epicDto);
    }

    @PatchMapping("/{epicId}")
    @ResponseStatus(HttpStatus.OK)
    @Validated({Marker.toUpdate.class})
    public EpicResponseDto updateEpicById(@PathVariable UUID epicId,
                                          @RequestBody @Valid EpicDto epicDto) {
        log.info("Вызов PATCH-операции: /epic/{epicId}");
        return epicService.updateEpicById(epicId, epicDto);
    }

    @DeleteMapping("/{boardId}/{epicId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEpicById(@PathVariable UUID boardId, @PathVariable UUID epicId) {
        log.info("Вызов DELETE-операции: /epic/{epicId}");
        epicService.deleteEpicById(boardId, epicId);
    }
}
