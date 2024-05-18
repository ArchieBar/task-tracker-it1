package ru.itone.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.itone.model.Marker;
import ru.itone.model.task.dto.TaskDto;
import ru.itone.model.task.dto.TaskResponseDto;
import ru.itone.service.task.TaskService;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@Slf4j
@Validated
@RestController
@RequestMapping("/task")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;

    @GetMapping("/all/{epicId}")
    @ResponseStatus(HttpStatus.OK)
    public List<TaskResponseDto> findTasksByEpicId(@PathVariable UUID epicId) {
        log.info("Вызов GET-операции: task/all/{epicId}");
        return taskService.findTasksByEpicId(epicId);
    }

    @GetMapping("/{taskId}")
    @ResponseStatus(HttpStatus.OK)
    public TaskResponseDto findTaskById(@PathVariable UUID taskId) {
        log.info("Вызов GET-операции: task/{taskId}");
        return taskService.findTaskById(taskId);
    }

    @PostMapping("/{epicId}")
    @ResponseStatus(HttpStatus.CREATED)
    @Validated({Marker.toCreate.class})
    public TaskResponseDto createTaskInEpic(@RequestHeader("X-User-Id") UUID userId,
                                            @PathVariable UUID epicId,
                                            @RequestBody @Valid TaskDto taskDto) {
        log.info("Вызов POST-операции: task/{epicId}");
        return taskService.createTaskById(userId, epicId, taskDto);
    }

    @PatchMapping("/{taskId}")
    @ResponseStatus(HttpStatus.OK)
    @Validated({Marker.toUpdate.class})
    public TaskResponseDto updateTaskById(@RequestHeader("X-User-Id") UUID userId,
                                          @PathVariable UUID taskId,
                                          @RequestBody @Valid TaskDto taskDto) {
        log.info("Вызов PATCH-операции: task/{taskId}");
        return taskService.updateTaskById(userId, taskId, taskDto);
    }

    @PatchMapping("/{epicId}/{taskId}")
    @ResponseStatus(HttpStatus.OK)
    public TaskResponseDto updateCompletedTask(@RequestHeader("X-User-Id") UUID userId,
                                               @PathVariable UUID epicId,
                                               @PathVariable UUID taskId,
                                               @RequestParam Boolean completed) {
        log.info("Вызов PATCH-операции: task/{epicId}/{taskId}");
        return taskService.updateCompletedTask(userId, epicId, taskId, completed);
    }

    @DeleteMapping("/{epicId}/{taskId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTaskById(@RequestHeader("X-User-Id") UUID userId,
                               @PathVariable UUID epicId,
                               @PathVariable UUID taskId) {
        log.info("Вызов DELETE-операции: task/{epicId}/{taskId}");
        taskService.deleteTaskById(userId, epicId, taskId);
    }
}
