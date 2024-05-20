package ru.itone.taskTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.itone.model.board.Board;
import ru.itone.model.board.dto.BoardDto;
import ru.itone.model.epic.Epic;
import ru.itone.model.epic.EpicStatus;
import ru.itone.model.epic.dto.EpicDto;
import ru.itone.model.task.Task;
import ru.itone.model.task.dto.TaskDto;
import ru.itone.model.task.dto.TaskResponseDto;
import ru.itone.model.user.Entitlement;
import ru.itone.model.user.EntitlementEnum;
import ru.itone.model.user.User;
import ru.itone.model.user.dto.RegisterFormDto;
import ru.itone.repository.EntitlementRepository;
import ru.itone.repository.EpicRepository;
import ru.itone.repository.TaskRepository;
import ru.itone.repository.UserRepository;
import ru.itone.service.task.TaskServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {
    @InjectMocks
    private TaskServiceImpl service;

    @Mock
    private EntitlementRepository entitlementRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private EpicRepository epicRepository;

    private User user;
    private UUID userId;

    private Epic epic;
    private UUID epicId;

    private TaskDto taskDto;
    private Task task;
    private UUID taskId;

    private Board board;
    private UUID boardId;

    private Entitlement entitlement;


    @BeforeEach
    public void setUp() {
        user = new User(
                new RegisterFormDto(
                        "Имя",
                        "Фамилия",
                        "email@example.com",
                        "password"
                )
        );
        userId = UUID.randomUUID();
        user.setId(userId);

        EpicDto epicDto = new EpicDto(
                "Имя",
                "Описание",
                LocalDateTime.now().plusDays(1)
        );

        epic = new Epic(epicDto, board);
        epicId = UUID.randomUUID();
        epic.setId(epicId);
        epic.addUser(user);

        taskDto = new TaskDto("Описание");
        task = new Task(taskDto, epic);
        taskId = UUID.randomUUID();
        task.setId(taskId);

        epic.getTasks().add(task);

        BoardDto boardDto = new BoardDto("Имя");
        board = new Board(boardDto);
        boardId = UUID.randomUUID();
        board.setId(boardId);
        board.addUser(user);
        board.addEpic(epic);

        epic.setBoard(board);

        entitlement = new Entitlement(board, user, EntitlementEnum.OWNER);
        UUID entitlementId = UUID.randomUUID();
        entitlement.setId(entitlementId);
        entitlement.setBoard(board);

        user.addEntitlement(entitlement);
    }

    @Test
    public void checkThatTheServiceMethodFindTasksByEpicIdReturnsDataCorrectlyWithTheParametersPassedCorrectly() {
        when(epicRepository.findById(epicId))
                .thenReturn(Optional.of(epic));

        List<TaskResponseDto> result = service.findTasksByEpicId(epicId);

        assertEquals(taskId, result.get(0).getId());
    }

    @Test
    public void checkThatTheServiceMethodFindTaskByIdReturnsDataCorrectlyWithTheParametersPassedCorrectly() {
        when(taskRepository.findById(taskId))
                .thenReturn(Optional.of(task));

        TaskResponseDto result = service.findTaskById(taskId);

        assertEquals(taskId, result.getId());
    }

    @Test
    public void checkThatTheServiceMethodCreateTaskByIdReturnsDataCorrectlyWithTheParametersPassedCorrectly() {
        when(epicRepository.findById(epicId))
                .thenReturn(Optional.of(epic));
        when(entitlementRepository.findByUserIdAndBoardId(userId, boardId))
                .thenReturn(Optional.of(entitlement));
        when(taskRepository.save(any(Task.class)))
                .thenReturn(task);
        when(epicRepository.save(any(Epic.class)))
                .thenReturn(epic);

        TaskResponseDto result = service.createTaskById(userId, epicId, taskDto);

        assertEquals(taskId, result.getId());
    }

    @Test
    public void checkThatTheServiceMethodUpdateTaskByIdReturnsDataCorrectlyWithTheParametersPassedCorrectly() {
        when(taskRepository.findById(taskId))
                .thenReturn(Optional.of(task));
        when(entitlementRepository.findByUserIdAndBoardId(userId, boardId))
                .thenReturn(Optional.of(entitlement));
        when(taskRepository.save(any(Task.class)))
                .thenReturn(task);

        TaskResponseDto result = service.updateTaskById(userId, taskId, taskDto);

        assertEquals(taskId, result.getId());
    }

    @Test
    public void checkThatTheServiceMethodUpdateCompletedTaskReturnsDataCorrectlyWithTheParametersPassedCorrectly() {
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(taskRepository.findById(taskId))
                .thenReturn(Optional.of(task));
        when(epicRepository.findById(epicId))
                .thenReturn(Optional.of(epic));
        when(entitlementRepository.findByUserIdAndBoardId(userId, boardId))
                .thenReturn(Optional.of(entitlement));

        task.setIsCompleted(true);

        when(taskRepository.save(task))
                .thenReturn(task);

        epic.setStatus(EpicStatus.DONE);

        when(epicRepository.save(epic))
                .thenReturn(epic);

        TaskResponseDto result = service.updateCompletedTask(userId, epicId, taskId, true);

        assertEquals(taskId, result.getId());
        assertEquals(true, result.getIsCompleted());
        verify(epicRepository, atLeastOnce()).save(epic);
    }

    @Test
    public void checkThatTheServiceMethodUpdateDeleteTaskReturnsDataCorrectlyWithTheParametersPassedCorrectly() {
        when(epicRepository.findById(epicId))
                .thenReturn(Optional.of(epic));
        when(taskRepository.findById(taskId))
                .thenReturn(Optional.of(task));
        when(entitlementRepository.findByUserIdAndBoardId(userId, boardId))
                .thenReturn(Optional.of(entitlement));

        epic.getTasks().remove(task);
        epic.setStatus(EpicStatus.TODO);

        when(epicRepository.save(epic))
                .thenReturn(epic);
        doNothing().when(taskRepository).deleteById(taskId);

        service.deleteTaskById(userId, epicId, taskId);

        verify(epicRepository, atLeastOnce()).findById(epicId);
        verify(taskRepository, atLeastOnce()).findById(taskId);
        verify(entitlementRepository, atLeastOnce()).findByUserIdAndBoardId(userId, boardId);
        verify(epicRepository, atLeastOnce()).save(epic);
        verify(taskRepository, atLeastOnce()).deleteById(taskId);
    }
}
