package ru.itone.taskTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.itone.exception.task.TaskByIdNotFoundException;
import ru.itone.model.board.Board;
import ru.itone.model.board.dto.BoardDto;
import ru.itone.model.epic.Epic;
import ru.itone.model.epic.EpicStatus;
import ru.itone.model.epic.dto.EpicDto;
import ru.itone.model.task.dto.TaskDto;
import ru.itone.model.task.dto.TaskResponseDto;
import ru.itone.model.user.Entitlement;
import ru.itone.model.user.EntitlementEnum;
import ru.itone.model.user.User;
import ru.itone.model.user.dto.RegisterFormDto;
import ru.itone.repository.*;
import ru.itone.service.task.TaskService;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
@ActiveProfiles("test")
public class TaskIntegrationServiceTest {
    private final TaskService service;
    private final EntitlementRepository entitlementRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final EpicRepository epicRepository;
    private final BoardRepository boardRepository;

    private UUID ownerId;
    private UUID epicId;

    @BeforeEach
    public void setUp() {
        RegisterFormDto registerFormDto = new RegisterFormDto(
                "Владелец",
                "Фамилия",
                "email@example.com",
                "12345"
        );
        User owner = new User(registerFormDto);
        owner = userRepository.save(owner);
        ownerId = owner.getId();

        BoardDto boardDto = new BoardDto("Имя Доски");
        Board board = new Board(boardDto);
        board.addUser(owner);
        board = boardRepository.save(board);

        Entitlement entitlement = new Entitlement(board, owner, EntitlementEnum.OWNER);
        entitlement = entitlementRepository.save(entitlement);

        owner.addEntitlement(entitlement);
        userRepository.save(owner);

        EpicDto epicDto = new EpicDto(
                "Название",
                "Описание",
                LocalDateTime.now().plusDays(1)
        );
        Epic epic = new Epic(epicDto, board);
        epic = epicRepository.save(epic);
        epicId = epic.getId();

        board.addEpic(epic);
        boardRepository.save(board);
    }

    @Test
    public void checkThatTheTaskIsCreatedCorrectlyAndItIsListedInTheEpic() {
        TaskDto taskDto = new TaskDto("Описание");

        TaskResponseDto result = service.createTaskById(ownerId, epicId, taskDto);

        assertNotNull(result.getId());
        assertEquals(taskDto.getDescription(), result.getDescription());
        assertEquals(false, result.getIsCompleted());

        Epic epic = epicRepository.findById(epicId)
                .orElseThrow();

        assertEquals(EpicStatus.TODO, epic.getStatus());
    }

    @Test
    public void checkThatTheTaskStatusIsBeingUpdatedCorrectlyAndTheEpicStatusIsBeingUpdatedAlongWithIt() {
        TaskDto taskDto = new TaskDto("Описание");
        UUID taskId = service.createTaskById(ownerId, epicId, taskDto).getId();

        TaskResponseDto taskResponseDto = service.updateCompletedTask(ownerId, epicId, taskId, true);

        Epic result = epicRepository.findById(epicId)
                .orElseThrow();

        assertEquals(true, taskResponseDto.getIsCompleted());
        assertEquals(EpicStatus.DONE, result.getStatus());
    }

    @Test
    public void checkThatTheTaskIsDeletedCorrectlyAndTheEpicStatusChangesWithItBasedOnTheRemainingList() {
        TaskDto taskDto = new TaskDto("Описание");
        UUID taskId = service.createTaskById(ownerId, epicId, taskDto).getId();

        TaskDto taskDtoSec = new TaskDto("Описание");
        service.createTaskById(ownerId, epicId, taskDtoSec);

        service.updateCompletedTask(ownerId, epicId, taskId, true);

        Epic interResult = epicRepository.findById(epicId)
                .orElseThrow();

        assertEquals(EpicStatus.DOING, interResult.getStatus());

        service.deleteTaskById(ownerId, epicId, taskId);

        Epic result = epicRepository.findById(epicId)
                .orElseThrow();

        assertEquals(EpicStatus.TODO, result.getStatus());
        assertThrows(TaskByIdNotFoundException.class, () -> {
            taskRepository.findById(taskId)
                    .orElseThrow(() -> new TaskByIdNotFoundException(taskId));
        });
    }
}
