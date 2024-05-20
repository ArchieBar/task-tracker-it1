package ru.itone.boardTest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.itone.model.board.Board;
import ru.itone.model.board.dto.BoardDto;
import ru.itone.model.board.dto.BoardResponseDto;
import ru.itone.model.board.invite.Invite;
import ru.itone.model.user.Entitlement;
import ru.itone.model.user.EntitlementEnum;
import ru.itone.model.user.User;
import ru.itone.model.user.dto.RegisterFormDto;
import ru.itone.repository.*;
import ru.itone.service.board.BoardServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BoardServiceTest {
    @InjectMocks
    private BoardServiceImpl service;

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
    private InviteRepository inviteRepository;

    @Test
    public void checkThatTheServiceMethodFindBoardsReturnsDataCorrectlyWithTheParametersPassedCorrectly() {
        Board board = new Board();
        UUID boardId = UUID.randomUUID();
        board.setId(boardId);

        Pageable pageable = PageRequest.of(0, 10);

        when(boardRepository.findAll(pageable))
                .thenReturn(new PageImpl<>(List.of(board)));

        List<BoardResponseDto> result = service.findBoards(pageable);

        assertEquals(boardId, result.get(0).getId());
    }

    @Test
    public void checkThatTheServiceMethodFindBoardByIdReturnsDataCorrectlyWithTheParametersPassedCorrectly() {
        Board board = new Board();
        UUID boardId = UUID.randomUUID();
        board.setId(boardId);

        when(boardRepository.findById(boardId))
                .thenReturn(Optional.of(board));

        BoardResponseDto result = service.findBoardById(boardId);

        assertEquals(boardId, result.getId());
    }

    @Test
    public void checkThatTheServiceMethodCreateBoardReturnsDataCorrectlyWithTheParametersPassedCorrectly() {
        BoardDto boardDto = new BoardDto("Имя");

        User user = new User(
                new RegisterFormDto(
                        "Имя",
                        "Фамилия",
                        "email@example.com",
                        "password"
                )
        );
        UUID userId = UUID.randomUUID();
        user.setId(userId);

        Board board = new Board(boardDto);
        UUID boardId = UUID.randomUUID();
        board.setId(boardId);
        board.addUser(user);

        Entitlement entitlement = new Entitlement(board, user, EntitlementEnum.OWNER);
        UUID entitlementId = UUID.randomUUID();
        entitlement.setId(entitlementId);

        user.addEntitlement(entitlement);

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(boardRepository.save(any()))
                .thenReturn(board);
        when(entitlementRepository.save(any()))
                .thenReturn(entitlement);
        when(userRepository.save(any()))
                .thenReturn(user);

        BoardResponseDto result = service.createBoard(userId, boardDto);

        assertEquals(boardId, result.getId());
        assertEquals(userId, result.getUsers().get(0).getId());
    }

    @Test
    public void checkThatTheServiceMethodInviteUserCallsAllTheNecessaryRepositoryMethodsAtLeastOnceWithCorrectlyPassedParameters() {
        UUID ownerId = UUID.randomUUID();

        User user = new User(
                new RegisterFormDto(
                        "Имя",
                        "Фамилия",
                        "email@example.com",
                        "password"
                )
        );
        UUID userId = UUID.randomUUID();
        user.setId(userId);

        Board board = new Board(new BoardDto("Имя"));
        UUID boardId = UUID.randomUUID();
        board.setId(boardId);
        board.addUser(user);

        Entitlement entitlement = new Entitlement(board, user, EntitlementEnum.OWNER);
        UUID entitlementId = UUID.randomUUID();
        entitlement.setId(entitlementId);

        user.addEntitlement(entitlement);

        Invite invite = new Invite(user, board);
        UUID inviteId = UUID.randomUUID();
        invite.setId(inviteId);

        when(entitlementRepository.findByUserIdAndBoardId(ownerId, boardId))
                .thenReturn(Optional.of(entitlement));
        when(boardRepository.findById(boardId))
                .thenReturn(Optional.of(board));
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(inviteRepository.save(any(Invite.class)))
                .thenReturn(invite);

        service.inviteUser(ownerId, boardId, userId);

        verify(entitlementRepository, atLeastOnce()).findByUserIdAndBoardId(ownerId, boardId);
        verify(boardRepository, atLeastOnce()).findById(boardId);
        verify(userRepository, atLeastOnce()).findById(userId);
        verify(inviteRepository, atLeastOnce()).save(any(Invite.class));
    }

    @Test
    public void checkThatTheServiceMethodIssueEntitlementCallsAllTheNecessaryRepositoryMethodsAtLeastOnceWithCorrectlyPassedParameters() {
        User owner = new User(
                new RegisterFormDto(
                        "Владелец",
                        "Фамилия",
                        "emailOwner@example.com",
                        "password"
                )
        );
        UUID ownerId = UUID.randomUUID();
        owner.setId(ownerId);

        User user = new User(
                new RegisterFormDto(
                        "Имя",
                        "Фамилия",
                        "email@example.com",
                        "password"
                )
        );
        UUID userId = UUID.randomUUID();
        user.setId(userId);

        Board board = new Board(new BoardDto("Имя"));
        UUID boardId = UUID.randomUUID();
        board.setId(boardId);
        board.addUser(user);

        Entitlement entitlementOwner = new Entitlement(board, user, EntitlementEnum.OWNER);
        UUID entitlementOwnerId = UUID.randomUUID();
        entitlementOwner.setId(entitlementOwnerId);

        Entitlement entitlement = new Entitlement(board, user, EntitlementEnum.USER);
        UUID entitlementId = UUID.randomUUID();
        entitlement.setId(entitlementId);

        Entitlement entitlementAfterSave = new Entitlement(board, user, EntitlementEnum.EDITOR);
        entitlementAfterSave.setId(entitlementId);

        user.addEntitlement(entitlement);

        when(entitlementRepository.findByUserIdAndBoardId(ownerId, boardId))
                .thenReturn(Optional.of(entitlementOwner));
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(entitlementRepository.findByUserIdAndBoardId(userId, boardId))
                .thenReturn(Optional.of(entitlement));
        when(entitlementRepository.save(any(Entitlement.class)))
                .thenReturn(entitlementAfterSave);

        service.issueEntitlement(ownerId, boardId, userId, EntitlementEnum.EDITOR);

        verify(entitlementRepository, atLeastOnce()).findByUserIdAndBoardId(ownerId, boardId);
        verify(entitlementRepository, atLeastOnce()).findByUserIdAndBoardId(userId, boardId);
        verify(userRepository, atLeastOnce()).findById(userId);
        verify(entitlementRepository, atLeastOnce()).save(any(Entitlement.class));
    }

    @Test
    public void checkThatTheServiceMethodUpdateBoardByIdReturnsDataCorrectlyWithTheParametersPassedCorrectly() {
        BoardDto boardDto = new BoardDto("Имя");

        User user = new User(
                new RegisterFormDto(
                        "Имя",
                        "Фамилия",
                        "email@example.com",
                        "password"
                )
        );
        UUID userId = UUID.randomUUID();
        user.setId(userId);

        Board board = new Board(new BoardDto("Старое имя"));
        UUID boardId = UUID.randomUUID();
        board.setId(boardId);
        board.addUser(user);

        Board boardAfterSave = new Board(boardDto);
        boardAfterSave.setId(boardId);
        boardAfterSave.addUser(user);

        Entitlement entitlement = new Entitlement(board, user, EntitlementEnum.OWNER);
        UUID entitlementId = UUID.randomUUID();
        entitlement.setId(entitlementId);

        user.addEntitlement(entitlement);

        when(entitlementRepository.findByUserIdAndBoardId(userId, boardId))
                .thenReturn(Optional.of(entitlement));
        when(boardRepository.findById(boardId))
                .thenReturn(Optional.of(board));
        when(boardRepository.save(any(Board.class)))
                .thenReturn(boardAfterSave);

        BoardResponseDto result = service.updateBoardById(userId, boardId, boardDto);

        assertEquals(boardId, result.getId());
    }

    @Test
    public void checkThatTheServiceMethodDeleteBoardByIdCallsAllTheNecessaryRepositoryMethodsAtLeastOnceWithCorrectlyPassedParameters() {
        User user = new User(
                new RegisterFormDto(
                        "Имя",
                        "Фамилия",
                        "email@example.com",
                        "password"
                )
        );
        UUID userId = UUID.randomUUID();
        user.setId(userId);

        Board board = new Board(new BoardDto("Имя"));
        UUID boardId = UUID.randomUUID();
        board.setId(boardId);
        board.addUser(user);

        Entitlement entitlement = new Entitlement(board, user, EntitlementEnum.OWNER);
        UUID entitlementId = UUID.randomUUID();
        entitlement.setId(entitlementId);

        user.addEntitlement(entitlement);

        when(entitlementRepository.findByUserIdAndBoardId(userId, boardId))
                .thenReturn(Optional.of(entitlement));
        when(boardRepository.findById(boardId))
                .thenReturn(Optional.of(board));
        when(inviteRepository.findAllByBoardId(boardId))
                .thenReturn(new ArrayList<>());
        doNothing().when(inviteRepository).deleteAll(anyList());
        doNothing().when(entitlementRepository).deleteAllByBoardId(boardId);
        doNothing().when(epicRepository).deleteAll(anySet());
        doNothing().when(boardRepository).deleteById(boardId);

        service.deleteBoardById(userId, boardId);

        verify(inviteRepository, atLeastOnce()).deleteAll(anyList());
        verify(entitlementRepository, atLeastOnce()).deleteAllByBoardId(boardId);
        verify(epicRepository, atLeastOnce()).deleteAll(anySet());
        verify(boardRepository, atLeastOnce()).deleteById(boardId);
    }
}
