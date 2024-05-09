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

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EpicResponseDto> findEpics(@RequestParam(name = "page", defaultValue = "0") int page,
                                           @RequestParam(name = "size", defaultValue = "10") int size) {
        log.info("Вызов GET-операции '/epic/findEpics'");

        Pageable pageable = PageRequest.of(page, size);

        return epicService.findEpics(pageable);
    }

    @GetMapping("/{epicId}")
    @ResponseStatus(HttpStatus.OK)
    public EpicResponseDto findEpicById(@PathVariable UUID epicId) {
        log.info("Вызов GET-операции '/epic/findEpicById'");
        return epicService.findEpicById(epicId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Validated({Marker.toCreate.class})
    public EpicResponseDto createEpic(@RequestBody @Valid EpicDto epicDto) {
        log.info("Вызов POST-операции '/epic/createEpic'");
        return epicService.createEpic(epicDto);
    }

    @PatchMapping("/{epicId}")
    @ResponseStatus(HttpStatus.OK)
    @Validated({Marker.toUpdate.class})
    public EpicResponseDto updateEpicById(@PathVariable UUID epicId,
                                          @RequestBody @Valid EpicDto epicDto) {
        log.info("Вызов PATCH-операции '/epic/updateEpicById'");
        return epicService.updateEpicById(epicId, epicDto);
    }

    @DeleteMapping("/{epicId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEpicById(@PathVariable UUID epicId) {
        log.info("Вызов DELETE-операции '/epic/deleteEpicById'");
        epicService.deleteEpicById(epicId);
    }
}
