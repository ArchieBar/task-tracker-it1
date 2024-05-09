package ru.itone.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.itone.model.Marker;
import ru.itone.model.task.dto.TaskDto;
import ru.itone.model.task.dto.TaskResponseDto;
import ru.itone.service.task.TaskService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

@Slf4j
@Validated
@RestController
@RequestMapping("/task")
public class TaskController {
    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    //TODO
    // Не уверен нужна ли тут пагинация
    @GetMapping("/{epicId}")
    @ResponseStatus(HttpStatus.OK)
    public List<TaskResponseDto> findTasksByEpicId(@PathVariable UUID epicId) {
        log.info("Вызов GET-операции 'task/findTasksByEpicId'");
        return taskService.findTasksByEpicId(epicId);
    }

    @GetMapping("/{epicId}/{taskId}")
    @ResponseStatus(HttpStatus.OK)
    public TaskResponseDto findTaskById(@PathVariable UUID epicId,
                                        @PathVariable UUID taskId) {
        log.info("Вызов GET-операции 'task/findTaskById'");
        return taskService.findTaskById(epicId, taskId);
    }

    @PostMapping("/{epicId}/created")
    @ResponseStatus(HttpStatus.CREATED)
    @Validated({Marker.toCreate.class})
    public TaskResponseDto createTaskInEpic(@PathVariable UUID epicId,
                                            @RequestBody @Valid TaskDto taskDto) {
        log.info("Вызов POST-операции 'task/createTaskInEpic'");
        return taskService.createTaskById(epicId, taskDto);
    }

    @PatchMapping("/{epicId}/update_task/{taskId}")
    @ResponseStatus(HttpStatus.OK)
    @Validated({Marker.toUpdate.class})
    public TaskResponseDto updateTaskById(@PathVariable UUID epicId,
                                          @PathVariable UUID taskId,
                                          @RequestBody @Valid TaskDto taskDto) {
        log.info("Вызов PATCH-операции 'task/updateTaskById'");
        return taskService.updateTaskById(epicId, taskId, taskDto);
    }

    @PatchMapping("/{epicId}/update_status/{taskId}")
    @ResponseStatus(HttpStatus.OK)
    public TaskResponseDto updateCompletedTask(@PathVariable UUID epicId,
                                               @PathVariable UUID taskId,
                                               @RequestBody @NotNull Boolean isCompleted) {
        log.info("Вызов PATCH-операции 'task/updateCompletedTask'");
        return taskService.updateCompletedTask(epicId, taskId, isCompleted);
    }

    @DeleteMapping("/{epicId}/delete/{taskId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTaskById(@PathVariable UUID epicId,
                               @PathVariable UUID taskId) {
        log.info("Вызов DELETE-операции 'task/deleteTaskById'");
        taskService.deleteTaskById(epicId, taskId);
    }
}
