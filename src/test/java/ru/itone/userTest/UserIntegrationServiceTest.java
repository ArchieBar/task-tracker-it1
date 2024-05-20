package ru.itone.userTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.itone.exception.user.UserByIdNotFoundException;
import ru.itone.exception.user.UserRightsByUserIdAndBoardIdNotFoundException;
import ru.itone.model.board.Board;
import ru.itone.model.board.dto.BoardDto;
import ru.itone.model.user.Entitlement;
import ru.itone.model.user.EntitlementEnum;
import ru.itone.model.user.User;
import ru.itone.model.user.dto.LoginFormDto;
import ru.itone.model.user.dto.RegisterFormDto;
import ru.itone.model.user.dto.UserResponseDto;
import ru.itone.repository.*;
import ru.itone.service.board.BoardService;
import ru.itone.service.user.UserService;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
@ActiveProfiles("test")
public class UserIntegrationServiceTest {
    private final UserService service;
    private final UserRepository userRepository;
    private final InviteRepository inviteRepository;
    private final BoardService boardService;
    private final BoardRepository boardRepository;
    private final EntitlementRepository entitlementRepository;
    private final CommentRepository commentRepository;

    private UUID ownerId;
    private UUID boardId;

    @BeforeEach
    public void setUp() {
        RegisterFormDto registerFormDto = new RegisterFormDto(
                "Владелец",
                "Фамилия",
                "email@email.com",
                "12345"
        );
        User owner = new User(registerFormDto);
        owner = userRepository.save(owner);
        ownerId = owner.getId();

        BoardDto boardDto = new BoardDto("Имя Доски");
        boardId = boardService.createBoard(ownerId, boardDto).getId();
    }

    @Test
    public void checkThatTheUserIsSavedCorrectly() {
        RegisterFormDto registerFormDto = new RegisterFormDto(
                "Владелец",
                "Фамилия",
                "email@example.com",
                "12345"
        );

        UserResponseDto result = service.registerUser(registerFormDto);

        assertNotNull(result.getId());
        assertEquals(registerFormDto.getFirstName() + " " + registerFormDto.getLastName(), result.getFullName());
        assertEquals(registerFormDto.getEmail(), result.getEmail());
    }

    @Test
    public void checkThatTheLoginIsCorrectAndTheDataIsDisplayedInTheDatabase() {
        RegisterFormDto registerFormDto = new RegisterFormDto(
                "Имя",
                "Фамилия",
                "email@example.com",
                "12345"
        );
        UUID userId = service.registerUser(registerFormDto).getId();
        User user = userRepository.findById(userId)
                .orElseThrow();
        user.setLogon(false);
        userRepository.save(user);

        LoginFormDto loginFormDto = new LoginFormDto(
                "email@example.com",
                "12345"
        );

        service.login(loginFormDto);

        User result = userRepository.findById(userId)
                .orElseThrow();

        assertEquals(true, result.getLogon());
    }

    @Test
    public void checkThatTheLogoutIsCorrectAndTheDataIsDisplayedInTheDatabase() {
        RegisterFormDto registerFormDto = new RegisterFormDto(
                "Имя",
                "Фамилия",
                "email@example.com",
                "12345"
        );
        UUID userId = service.registerUser(registerFormDto).getId();

        service.logout(userId);

        User result = userRepository.findById(userId)
                .orElseThrow();

        assertEquals(false, result.getLogon());
    }

    @Test
    public void checkThatTheAcceptanceOfTheInvitationToTheBoardIsCorrectAndTheUserIsListedOnTheBoard() {
        RegisterFormDto registerFormDto = new RegisterFormDto(
                "Имя",
                "Фамилия",
                "email@example.com",
                "12345"
        );
        UUID userId = service.registerUser(registerFormDto).getId();

        boardService.inviteUser(ownerId, boardId, userId);

        service.confirmInvite(userId, boardId);

        Board board = boardRepository.findById(boardId)
                .orElseThrow();
        Entitlement entitlement = entitlementRepository.findByUserIdAndBoardId(userId, boardId)
                .orElseThrow();

        assertEquals(2, board.getUsers().size());
        assertEquals(EntitlementEnum.USER, entitlement.getEntitlement());
    }

    @Test
    public void checkThatTheUserIsDeletedCorrectlyAndTheOwnerRightsAreAssignedToAnotherUser() {
        RegisterFormDto registerFormDto = new RegisterFormDto(
                "Имя",
                "Фамилия",
                "email@example.com",
                "12345"
        );
        UUID userId = service.registerUser(registerFormDto).getId();

        boardService.inviteUser(ownerId, boardId, userId);

        service.confirmInvite(userId, boardId);

        service.deleteUserById(ownerId);

        Board board = boardRepository.findById(boardId)
                .orElseThrow();
        Entitlement entitlement = entitlementRepository.findByUserIdAndBoardId(userId, boardId)
                .orElseThrow();

        assertEquals(1, board.getUsers().size());
        assertEquals(EntitlementEnum.OWNER, entitlement.getEntitlement());
        assertThrows(UserByIdNotFoundException.class, () -> {
            userRepository.findById(ownerId)
                    .orElseThrow(() -> new UserByIdNotFoundException(ownerId));
        });
        assertThrows(UserRightsByUserIdAndBoardIdNotFoundException.class, () -> {
            entitlementRepository.findByUserIdAndBoardId(ownerId, boardId)
                    .orElseThrow(() -> new UserRightsByUserIdAndBoardIdNotFoundException(ownerId, boardId));
        });
    }
}
