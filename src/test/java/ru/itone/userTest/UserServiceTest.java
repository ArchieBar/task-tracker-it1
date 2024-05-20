package ru.itone.userTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.itone.model.board.Board;
import ru.itone.model.board.dto.BoardDto;
import ru.itone.model.board.dto.BoardResponseDto;
import ru.itone.model.board.invite.Invite;
import ru.itone.model.user.Entitlement;
import ru.itone.model.user.EntitlementEnum;
import ru.itone.model.user.User;
import ru.itone.model.user.dto.LoginFormDto;
import ru.itone.model.user.dto.RegisterFormDto;
import ru.itone.model.user.dto.UserDto;
import ru.itone.model.user.dto.UserResponseDto;
import ru.itone.repository.*;
import ru.itone.service.board.BoardService;
import ru.itone.service.user.UserServiceImpl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @InjectMocks
    private UserServiceImpl service;

    @Mock
    private UserRepository userRepository;

    @Mock
    private InviteRepository inviteRepository;

    @Mock
    private BoardService boardService;

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private EntitlementRepository entitlementRepository;

    @Mock
    private CommentRepository commentRepository;

    private RegisterFormDto registerFormDto;
    private User user;
    private UUID userId;

    private Board board;
    private UUID boardId;

    private Entitlement entitlement;

    private Invite invite;

    @BeforeEach
    public void setUp() {
        registerFormDto = new RegisterFormDto(
                "Имя",
                "Фамилия",
                "email@example.com",
                "12345"
        );
        user = new User(registerFormDto);
        userId = UUID.randomUUID();
        user.setId(userId);

        BoardDto boardDto = new BoardDto("Имя");

        board = new Board(boardDto);
        boardId = UUID.randomUUID();
        board.setId(boardId);
        board.addUser(user);

        entitlement = new Entitlement(board, user, EntitlementEnum.OWNER);
        UUID entitlementId = UUID.randomUUID();
        entitlement.setId(entitlementId);
        entitlement.setBoard(board);

        user.addEntitlement(entitlement);

        invite = new Invite(user, board);
        UUID inviteId = UUID.randomUUID();
        invite.setId(inviteId);
    }

    @Test
    public void checkThatTheServiceMethodFindUserByIdReturnsDataCorrectlyWithTheParametersPassedCorrectly() {
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        UserResponseDto result = service.findUserById(userId);

        assertEquals(userId, result.getId());
    }

    @Test
    public void checkThatTheServiceMethodFindInviteByUserReturnsDataCorrectlyWithTheParametersPassedCorrectly() {
        when(inviteRepository.findAllByUserIdAndConfirmed(userId, false))
                .thenReturn(List.of(invite));

        List<BoardResponseDto> result = service.findInviteByUser(userId);

        assertEquals(boardId, result.get(0).getId());
    }

    @Test
    public void checkThatTheServiceMethodRegisterUserReturnsDataCorrectlyWithTheParametersPassedCorrectly() {
        when(userRepository.save(any(User.class)))
                .thenReturn(user);

        UserResponseDto result = service.registerUser(registerFormDto);

        assertEquals(userId, result.getId());
    }

    @Test
    public void checkThatTheServiceMethodLoginReturnsDataCorrectlyWithTheParametersPassedCorrectly() {
        user.setLogon(false);

        when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class)))
                .thenReturn(user);

        UserResponseDto result = service.login(new LoginFormDto(user.getEmail(), user.getPassword()));

        assertEquals(userId, result.getId());
    }

    @Test
    public void checkThatTheServiceMethodLogoutReturnsDataCorrectlyWithTheParametersPassedCorrectly() {
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class)))
                .thenReturn(user);

        UserResponseDto result = service.logout(userId);

        assertEquals(userId, result.getId());
    }

    @Test
    public void checkThatTheServiceMethodConfirmInviteCallsAllTheNecessaryRepositoryMethodsAtLeastOnceWithCorrectlyPassedParameters() {
        when(inviteRepository.findByUserIdAndBoardId(userId, boardId))
                .thenReturn(Optional.of(invite));
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(boardRepository.findById(boardId))
                .thenReturn(Optional.of(board));
        when(inviteRepository.save(any(Invite.class)))
                .thenReturn(invite);
        when(entitlementRepository.save(any(Entitlement.class)))
                .thenReturn(entitlement);
        when(boardRepository.save(any(Board.class)))
                .thenReturn(board);

        service.confirmInvite(userId, boardId);

        verify(inviteRepository, atLeastOnce()).findByUserIdAndBoardId(userId, boardId);
        verify(userRepository, atLeastOnce()).findById(userId);
        verify(boardRepository, atLeastOnce()).findById(boardId);
        verify(inviteRepository, atLeastOnce()).save(any(Invite.class));
        verify(entitlementRepository, atLeastOnce()).save(any(Entitlement.class));
        verify(boardRepository, atLeastOnce()).save(any(Board.class));
    }

    @Test
    public void checkThatTheServiceMethodUpdateUserByIdReturnsDataCorrectlyWithTheParametersPassedCorrectly() {
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class)))
                .thenReturn(user);

        UserDto userDto = new UserDto(
                "Имя",
                "Фамилия",
                "email@example.com"
        );

        UserResponseDto result = service.updateUserById(userId, userDto);

        assertEquals(userId, result.getId());
    }

    @Test
    public void checkThatTheServiceMethodDeleteUserByIdCallsAllTheNecessaryRepositoryMethodsAtLeastOnceWithCorrectlyPassedParameters() {
        when(entitlementRepository.findAllByUserIdAndEntitlement(userId, EntitlementEnum.OWNER))
                .thenReturn(List.of(entitlement));
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(entitlementRepository.findAllByBoardId(boardId))
                .thenReturn(List.of(entitlement));
        when(boardRepository.findById(boardId))
                .thenReturn(Optional.of(board));
        when(boardRepository.save(any(Board.class)))
                .thenReturn(board);
        doNothing().when(boardService).deleteBoardById(userId, boardId);
        doNothing().when(entitlementRepository).deleteAllByUserId(userId);
        doNothing().when(commentRepository).deleteAllByAuthorId(userId);
        doNothing().when(userRepository).deleteById(userId);

        service.deleteUserById(userId);

        verify(entitlementRepository, atLeastOnce()).findAllByUserIdAndEntitlement(userId, EntitlementEnum.OWNER);
        verify(userRepository, atLeastOnce()).findById(userId);
        verify(entitlementRepository, atLeastOnce()).findAllByBoardId(boardId);
        verify(boardRepository, atLeastOnce()).findById(boardId);
        verify(boardRepository, atLeastOnce()).save(any(Board.class));
        verify(boardService, atLeastOnce()).deleteBoardById(userId, boardId);
        verify(entitlementRepository, atLeastOnce()).deleteAllByUserId(userId);
        verify(commentRepository, atLeastOnce()).deleteAllByAuthorId(userId);
        verify(userRepository, atLeastOnce()).deleteById(userId);
    }
}
