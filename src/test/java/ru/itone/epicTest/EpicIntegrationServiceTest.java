package ru.itone.epicTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.itone.exception.epic.EpicByIdNotFoundException;
import ru.itone.exception.epic.comment.CommentByIdNotFoundException;
import ru.itone.model.board.Board;
import ru.itone.model.board.dto.BoardDto;
import ru.itone.model.epic.Epic;
import ru.itone.model.epic.EpicStatus;
import ru.itone.model.epic.comment.dto.CommentDto;
import ru.itone.model.epic.dto.EpicDto;
import ru.itone.model.epic.dto.EpicResponseDto;
import ru.itone.model.user.Entitlement;
import ru.itone.model.user.EntitlementEnum;
import ru.itone.model.user.User;
import ru.itone.model.user.dto.RegisterFormDto;
import ru.itone.repository.*;
import ru.itone.service.epic.EpicService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
@ActiveProfiles("test")
public class EpicIntegrationServiceTest {
    private final EpicService service;
    private final UserRepository userRepository;
    private final EntitlementRepository entitlementRepository;
    private final BoardRepository boardRepository;
    private final EpicRepository epicRepository;
    private final CommentRepository commentRepository;

    private UUID ownerId;
    private UUID boardId;

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
        boardId = board.getId();

        Entitlement entitlement = new Entitlement(board, owner, EntitlementEnum.OWNER);
        entitlement = entitlementRepository.save(entitlement);

        owner.addEntitlement(entitlement);
        userRepository.save(owner);
    }

    @Test
    public void checkThatTheEpicIsBeingCreatedCorrectlyAndIsListedOnTheBoard() {
        EpicDto epicDto = new EpicDto(
                "Название",
                "Описание",
                LocalDateTime.now().plusDays(1)
        );

        EpicResponseDto result = service.createEpic(ownerId, boardId, epicDto);

        assertNotNull(result.getId());
        assertEquals(epicDto.getName(), result.getName());
        assertEquals(epicDto.getDescription(), result.getDescription());
        assertEquals(epicDto.getEndTime(), result.getEndTime());
        assertNotNull(result.getCreatedTime());
        assertNotNull(result.getUsers());
        assertNotNull(result.getActivity());
        assertEquals(EpicStatus.TODO.toString(), result.getStatus());

        Board board = boardRepository.findById(boardId).orElseThrow();
        List<Epic> epics = board.getEpics().stream()
                .filter(epic -> epic.getId().equals(result.getId()))
                .collect(Collectors.toList());

        assertFalse(epics.isEmpty());
        assertEquals(1, epics.size());
    }

    @Test
    public void checkThatTheCommentIsBeingCreatedSuccessfullyAndIsListedInTheEpic() {
        EpicDto epicDto = new EpicDto(
                "Название",
                "Описание",
                LocalDateTime.now().plusDays(1)
        );
        UUID epicId = service.createEpic(ownerId, boardId, epicDto).getId();

        CommentDto commentDto = new CommentDto("Текст");
        service.createCommentByEpicId(ownerId, epicId, commentDto);

        Epic result = epicRepository.findById(epicId)
                .orElseThrow();

        assertEquals(commentDto.getText(), result.getActivity().get(0).getText());
        assertNotNull(result.getActivity().get(0).getId());
        assertEquals(ownerId, result.getActivity().get(0).getAuthor().getId());
    }

    @Test
    public void checkThatTheEpicIsDeletedCorrectlyAndTheEpicIsNoLongerStoredInTheBoard() {
        EpicDto epicDto = new EpicDto(
                "Название",
                "Описание",
                LocalDateTime.now().plusDays(1)
        );
        UUID epicId = service.createEpic(ownerId, boardId, epicDto).getId();

        CommentDto commentDto = new CommentDto("Текст");
        UUID commentId = service.createCommentByEpicId(ownerId, epicId, commentDto).getId();

        service.deleteEpicById(ownerId, boardId, epicId);

        Board result = boardRepository.findById(boardId)
                .orElseThrow();

        assertTrue(result.getEpics().isEmpty());
        assertThrows(EpicByIdNotFoundException.class, () -> {
            epicRepository.findById(epicId)
                    .orElseThrow(() -> new EpicByIdNotFoundException(epicId));
        });
        assertThrows(CommentByIdNotFoundException.class, () -> {
            commentRepository.findById(commentId)
                    .orElseThrow(() -> new CommentByIdNotFoundException(commentId));
        });
    }

    @Test
    public void checkThatTheCommentIsDeletedCorrectlyAndIsNotListedInTheEpic() {
        EpicDto epicDto = new EpicDto(
                "Название",
                "Описание",
                LocalDateTime.now().plusDays(1)
        );
        UUID epicId = service.createEpic(ownerId, boardId, epicDto).getId();

        CommentDto commentDto = new CommentDto("Текст");
        UUID commentId = service.createCommentByEpicId(ownerId, epicId, commentDto).getId();

        service.deleteCommentById(ownerId, epicId, commentId);

        Epic result = epicRepository.findById(epicId)
                .orElseThrow();

        assertTrue(result.getActivity().isEmpty());
        assertThrows(CommentByIdNotFoundException.class, () -> {
            commentRepository.findById(commentId)
                    .orElseThrow(() -> new CommentByIdNotFoundException(commentId));
        });
    }
}
