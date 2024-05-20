package ru.itone.boardTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.itone.exception.board.BoardByIdNotFoundException;
import ru.itone.model.board.Board;
import ru.itone.model.board.dto.BoardDto;
import ru.itone.model.board.dto.BoardResponseDto;
import ru.itone.model.board.invite.Invite;
import ru.itone.model.epic.Epic;
import ru.itone.model.epic.dto.EpicDto;
import ru.itone.model.user.Entitlement;
import ru.itone.model.user.EntitlementEnum;
import ru.itone.model.user.User;
import ru.itone.model.user.dto.RegisterFormDto;
import ru.itone.repository.*;
import ru.itone.service.board.BoardService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
@ActiveProfiles("test")
public class BoardIntegrationServiceTest {
    private final BoardService service;
    private final UserRepository userRepository;
    private final EntitlementRepository entitlementRepository;
    private final BoardRepository boardRepository;
    private final EpicRepository epicRepository;
    private final InviteRepository inviteRepository;

    private UUID ownerId;

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
    }

    @Test
    public void checkThatTheBoardIsSavedCorrectlyTheIdIsAssignedAndTheUserRightsAreAssigned() {
        BoardDto boardDto = new BoardDto("Имя Доски");

        BoardResponseDto result = service.createBoard(ownerId, boardDto);

        assertNotNull(result.getId());
        assertEquals(boardDto.getName(), result.getName());
        assertNotNull(result.getEpics());
        assertNotNull(result.getUsers());

        Entitlement entitlement = entitlementRepository.findByUserIdAndBoardId(ownerId, result.getId())
                .orElseThrow();

        assertEquals(EntitlementEnum.OWNER, entitlement.getEntitlement());
    }

    @Test
    public void checkThatTheInvitationWithTheOwnerRulesIsSentCorrectlyAndIsListedInTheDatabase() {
        RegisterFormDto registerFormDto = new RegisterFormDto(
                "Пользователь",
                "Фамилия",
                "exmaple@email.ru",
                "12345"
        );
        User user = new User(registerFormDto);
        user = userRepository.save(user);
        UUID userId = user.getId();

        BoardDto boardDto = new BoardDto("Имя Доски");
        UUID boardId = service.createBoard(ownerId, boardDto).getId();

        service.inviteUser(ownerId, boardId, userId);

        Invite result = inviteRepository.findByUserIdAndBoardId(userId, boardId)
                .orElseThrow();

        assertNotNull(result.getId());
        assertEquals(boardId, result.getBoard().getId());
        assertEquals(userId, result.getUser().getId());
        assertEquals(false, result.getConfirmed());
    }

    @Test
    public void checkThatTheUserRightsAreAssignedCorrectly() {
        RegisterFormDto registerFormDto = new RegisterFormDto(
                "Пользователь",
                "Фамилия",
                "exmaple@email.ru",
                "12345"
        );
        User user = new User(registerFormDto);
        user = userRepository.save(user);
        UUID userId = user.getId();

        BoardDto boardDto = new BoardDto("Имя Доски");
        UUID boardId = service.createBoard(ownerId, boardDto).getId();
        Board board = boardRepository.findById(boardId)
                .orElseThrow();

        board.addUser(user);
        board = boardRepository.save(board);

        Entitlement newEntitlement = new Entitlement(board, user, EntitlementEnum.USER);
        newEntitlement = entitlementRepository.save(newEntitlement);

        user.addEntitlement(newEntitlement);
        userRepository.save(user);

        service.issueEntitlement(ownerId, boardId, userId, EntitlementEnum.ADMIN);

        Entitlement result = entitlementRepository.findByUserIdAndBoardId(userId, boardId)
                .orElseThrow();

        assertEquals(userId, result.getUser().getId());
        assertEquals(boardId, result.getBoard().getId());
        assertEquals(EntitlementEnum.ADMIN, result.getEntitlement());
    }

    @Test
    public void toCheckThatTheDeletionIsHappeningCorrectlyAndTheUserRightsAreAlsoBeingDeleteAndTheMethodsForDeletingEntitiesAreAllCalled() {
        RegisterFormDto registerFormDto = new RegisterFormDto(
                "Пользователь",
                "Фамилия",
                "exmaple@email.ru",
                "12345"
        );
        User user = new User(registerFormDto);
        user = userRepository.save(user);
        UUID userId = user.getId();

        BoardDto boardDto = new BoardDto("Имя Доски");
        UUID boardId = service.createBoard(ownerId, boardDto).getId();
        Board board = boardRepository.findById(boardId)
                .orElseThrow();

        EpicDto epicDto = new EpicDto(
                "Название",
                "Описание",
                LocalDateTime.now().plusDays(1)
        );
        Epic epic = new Epic(epicDto, board);
        epicRepository.save(epic);

        board.addEpic(epic);
        board.addUser(user);
        board = boardRepository.save(board);

        Entitlement newEntitlement = new Entitlement(board, user, EntitlementEnum.USER);
        newEntitlement = entitlementRepository.save(newEntitlement);

        user.addEntitlement(newEntitlement);
        userRepository.save(user);

        service.deleteBoardById(ownerId, boardId);

        assertThrows(BoardByIdNotFoundException.class, () -> {
            boardRepository.findById(boardId).orElseThrow(() -> new BoardByIdNotFoundException(boardId));
        });

        List<Entitlement> entitlements = entitlementRepository.findAllByBoardId(boardId);
        assertTrue(entitlements.isEmpty());

        List<Invite> invitations = inviteRepository.findAllByBoardId(boardId);
        assertTrue(invitations.isEmpty());

        List<Epic> epics = epicRepository.findAll();
        assertTrue(epics.isEmpty());
    }
}
