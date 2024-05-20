package ru.itone.epicTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.itone.model.board.Board;
import ru.itone.model.board.dto.BoardDto;
import ru.itone.model.epic.Epic;
import ru.itone.model.epic.comment.Comment;
import ru.itone.model.epic.comment.dto.CommentDto;
import ru.itone.model.epic.comment.dto.CommentResponseDto;
import ru.itone.model.epic.dto.EpicDto;
import ru.itone.model.epic.dto.EpicResponseDto;
import ru.itone.model.user.Entitlement;
import ru.itone.model.user.EntitlementEnum;
import ru.itone.model.user.User;
import ru.itone.model.user.dto.RegisterFormDto;
import ru.itone.repository.*;
import ru.itone.service.epic.EpicServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EpicServiceTest {
    @InjectMocks
    private EpicServiceImpl service;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EntitlementRepository entitlementRepository;

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private EpicRepository epicRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private CommentRepository commentRepository;

    private User user;
    private UUID userId;

    private Entitlement entitlement;

    private Board board;
    private UUID boardId;

    private EpicDto epicDto;
    private Epic epic;
    private UUID epicId;

    private CommentDto commentDto;
    private Comment comment;
    private UUID commentId;

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

        epicDto = new EpicDto(
                "Имя",
                "Описание",
                LocalDateTime.now().plusDays(1)
        );

        epic = new Epic(epicDto, board);
        epicId = UUID.randomUUID();
        epic.setId(epicId);
        epic.addUser(user);

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

        commentDto = new CommentDto("Текст");
        comment = new Comment(commentDto, user, epic);
        commentId = UUID.randomUUID();
        comment.setId(commentId);
        comment.setAuthor(user);

        epic.addComment(comment);
    }

    @Test
    public void checkThatTheServiceMethodFindEpicsByBoardIdReturnsDataCorrectlyWithTheParametersPassedCorrectly() {
        when(boardRepository.findById(boardId))
                .thenReturn(Optional.of(board));

        List<EpicResponseDto> result = service.findEpicsByBoardId(boardId);

        assertEquals(epicId, result.get(0).getId());
    }

    @Test
    public void checkThatTheServiceMethodFindEpicByIdReturnsDataCorrectlyWithTheParametersPassedCorrectly() {
        when(epicRepository.findById(epicId))
                .thenReturn(Optional.of(epic));

        EpicResponseDto result = service.findEpicById(epicId);

        assertEquals(epicId, result.getId());
    }

    @Test
    public void checkThatTheServiceMethodCreateEpicReturnsDataCorrectlyWithTheParametersPassedCorrectly() {
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(boardRepository.findById(boardId))
                .thenReturn(Optional.of(board));
        when(epicRepository.save(any(Epic.class)))
                .thenReturn(epic);
        when(boardRepository.save(any(Board.class)))
                .thenReturn(board);

        EpicResponseDto result = service.createEpic(userId, boardId, epicDto);

        assertEquals(epicId, result.getId());
    }

    @Test
    public void checkThatTheServiceMethodCreateCommentByEpicIdReturnsDataCorrectlyWithTheParametersPassedCorrectly() {
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(epicRepository.findById(epicId))
                .thenReturn(Optional.of(epic));
        when(commentRepository.save(any(Comment.class)))
                .thenReturn(comment);
        when(epicRepository.save(any(Epic.class)))
                .thenReturn(any(Epic.class));

        CommentResponseDto result = service.createCommentByEpicId(userId, epicId, commentDto);

        assertEquals(commentId, result.getId());
    }

    @Test
    public void checkThatTheServiceMethodUpdateEpicByIdReturnsDataCorrectlyWithTheParametersPassedCorrectly() {
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(epicRepository.findById(epicId))
                .thenReturn(Optional.of(epic));
        when(epicRepository.save(any(Epic.class)))
                .thenReturn(epic);

        EpicResponseDto result = service.updateEpicById(userId, epicId, epicDto);

        assertEquals(epicId, result.getId());
    }

    @Test
    public void checkThatTheServiceMethodTakeEpicCallsAllTheNecessaryRepositoryMethodsAtLeastOnceWithCorrectlyPassedParameters() {
        when(epicRepository.findById(epicId))
                .thenReturn(Optional.of(epic));
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(entitlementRepository.findByUserIdAndBoardId(userId, boardId))
                .thenReturn(Optional.of(entitlement));
        when(epicRepository.save(any(Epic.class)))
                .thenReturn(epic);

        service.takeEpic(userId, epicId);

        verify(epicRepository, atLeastOnce()).findById(epicId);
        verify(userRepository, atLeastOnce()).findById(userId);
        verify(entitlementRepository, atLeastOnce()).findByUserIdAndBoardId(userId, boardId);
        verify(epicRepository, atLeastOnce()).save(any(Epic.class));
    }

    @Test
    public void checkThatTheServiceMethodRefuseEpicCallsAllTheNecessaryRepositoryMethodsAtLeastOnceWithCorrectlyPassedParameters() {
        when(epicRepository.findById(epicId))
                .thenReturn(Optional.of(epic));
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(epicRepository.save(any(Epic.class)))
                .thenReturn(epic);

        service.refuseEpic(userId, epicId);

        verify(epicRepository, atLeastOnce()).findById(epicId);
        verify(userRepository, atLeastOnce()).findById(userId);
        verify(epicRepository, atLeastOnce()).save(any(Epic.class));
    }

    @Test
    public void checkThatTheServiceMethodUpdateCommentByIdReturnsDataCorrectlyWithTheParametersPassedCorrectly() {
        when(commentRepository.findById(commentId))
                .thenReturn(Optional.of(comment));
        when(commentRepository.save(any(Comment.class)))
                .thenReturn(comment);

        CommentResponseDto result = service.updateCommentById(userId, commentId, commentDto);

        assertEquals(commentId, result.getId());
    }

    @Test
    public void checkThatTheServiceMethodDeleteEpicByIdCallsAllTheNecessaryRepositoryMethodsAtLeastOnceWithCorrectlyPassedParameters() {
        when(entitlementRepository.findByUserIdAndBoardId(userId, boardId))
                .thenReturn(Optional.of(entitlement));
        when(boardRepository.findById(boardId))
                .thenReturn(Optional.of(board));
        when(boardRepository.save(any(Board.class)))
                .thenReturn(board);
        doNothing().when(taskRepository).deleteAll(epic.getTasks());
        doNothing().when(epicRepository).deleteById(epicId);

        service.deleteEpicById(userId, boardId, epicId);

        verify(entitlementRepository, atLeastOnce()).findByUserIdAndBoardId(userId, boardId);
        verify(boardRepository, atLeastOnce()).findById(boardId);
        verify(boardRepository, atLeastOnce()).save(any(Board.class));
        verify(taskRepository, atLeastOnce()).deleteAll(epic.getTasks());
        verify(epicRepository, atLeastOnce()).deleteById(epicId);
    }

    @Test
    public void checkThatTheServiceMethodDeleteCommentByIdCallsAllTheNecessaryRepositoryMethodsAtLeastOnceWithCorrectlyPassedParameters() {
        when(epicRepository.findById(epicId))
                .thenReturn(Optional.of(epic));
        when(entitlementRepository.findByUserIdAndBoardId(userId, boardId))
                .thenReturn(Optional.of(entitlement));
        when(epicRepository.save(any(Epic.class)))
                .thenReturn(epic);
        doNothing().when(commentRepository).deleteById(commentId);

        service.deleteCommentById(userId, epicId, commentId);

        verify(epicRepository, atLeastOnce()).findById(epicId);
        verify(entitlementRepository, atLeastOnce()).findByUserIdAndBoardId(userId, boardId);
        verify(epicRepository, atLeastOnce()).save(any(Epic.class));
        verify(commentRepository, atLeastOnce()).deleteById(commentId);
    }
}
