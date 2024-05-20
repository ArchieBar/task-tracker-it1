package ru.itone.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.itone.exception.board.BoardByIdNotFoundException;
import ru.itone.exception.user.*;
import ru.itone.model.board.Board;
import ru.itone.model.board.BoardMapper;
import ru.itone.model.board.dto.BoardResponseDto;
import ru.itone.model.board.invite.Invite;
import ru.itone.model.user.Entitlement;
import ru.itone.model.user.EntitlementEnum;
import ru.itone.model.user.User;
import ru.itone.model.user.UserMapper;
import ru.itone.model.user.dto.LoginFormDto;
import ru.itone.model.user.dto.RegisterFormDto;
import ru.itone.model.user.dto.UserDto;
import ru.itone.model.user.dto.UserResponseDto;
import ru.itone.repository.*;
import ru.itone.service.board.BoardService;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final InviteRepository inviteRepository;
    private final BoardService boardService;
    private final BoardRepository boardRepository;
    private final EntitlementRepository entitlementRepository;
    private final CommentRepository commentRepository;

    /**
     * Находит пользователя по его Id.
     *
     * @param userId Id пользователя.
     * @return DTO объект пользователя
     * @throws UserByIdNotFoundException если пользователь не найден.
     */
    @Override
    public UserResponseDto findUserById(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserByIdNotFoundException(userId));

        return UserMapper.toUserResponseDto(user);
    }

    /**
     * Находит все приглашения пользователя.
     *
     * @param userId Id владельца запроса.
     * @return DTO список досок в которые приглашен пользователь.
     */
    @Override
    public List<BoardResponseDto> findInviteByUser(UUID userId) {
        List<Invite> invitations = inviteRepository.findAllByUserIdAndConfirmed(userId, false);

        return invitations.stream()
                .map(i -> BoardMapper.toBoardResponseDto(i.getBoard()))
                .collect(Collectors.toList());
    }


    /**
     * Регистрирует нового пользователя на основе DTO объекта.
     *
     * @param registerFormDto DTO объект содержащий информацию о новом пользователе
     * @return DTO объект нового пользователя.
     */
    @Override
    public UserResponseDto registerUser(RegisterFormDto registerFormDto) {
        User user = new User(registerFormDto);

        userRepository.save(user);

        return UserMapper.toUserResponseDto(user);
    }

    /**
     * Производит вход в систему по почте и паролю пользователя.
     *
     * @param loginFormDto DTO объект содержащий почту и пароль пользователя.
     * @return DTO объект пользователя.
     * @throws UserByEmailNotFoundException       если пользователь не найден по почте.
     * @throws UserInvalidPassword                если пароль не верен.
     * @throws UserLoginHasBeenCompletedException если пользователь уже вошел в систему ранее.
     */
    @Override
    public UserResponseDto login(LoginFormDto loginFormDto) {
        User user = userRepository.findByEmail(loginFormDto.getEmail())
                .orElseThrow(() -> new UserByEmailNotFoundException(loginFormDto.getEmail()));

        if (!Objects.equals(user.getPassword(), loginFormDto.getPassword())) {
            throw new UserInvalidPassword();
        }

        if (user.getLogon()) {
            throw new UserLoginHasBeenCompletedException();
        }

        user.setLogon(true);
        userRepository.save(user);

        return UserMapper.toUserResponseDto(user);
    }

    /**
     * Позволяет пользователю выйти из системы.
     *
     * @param userId Id владельца запроса.
     * @return DTO объект пользователя.
     * @throws UserByIdNotFoundException если пользователь не найден.
     */
    @Override
    public UserResponseDto logout(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserByIdNotFoundException(userId));

        user.setLogon(false);
        userRepository.save(user);

        return UserMapper.toUserResponseDto(user);
    }


    /**
     * Позволяет пользователю подтвердить приглашение в доску.
     *
     * @param userId  Id владельца запроса.
     * @param boardId Id доски в которую необходимо принять приглашение.
     * @throws InviteByUserIdAndBoardIdNotFoundException если приглашение не найдено.
     * @throws UserByIdNotFoundException                 если пользователь не найден.
     * @throws BoardByIdNotFoundException                если доска не найдена.
     */
    @Override
    public void confirmInvite(UUID userId, UUID boardId) {
        Invite invite = inviteRepository.findByUserIdAndBoardId(userId, boardId)
                .orElseThrow(() -> new InviteByUserIdAndBoardIdNotFoundException(userId, boardId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserByIdNotFoundException(userId));

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardByIdNotFoundException(boardId));

        invite.setConfirmed(true);
        inviteRepository.save(invite);

        Entitlement entitlement = new Entitlement(board, user, EntitlementEnum.USER);
        entitlement = entitlementRepository.save(entitlement);
        user.addEntitlement(entitlement);

        board.addUser(user);
        boardRepository.save(board);
    }


    /**
     * Обновляет существующего пользователя по Id.
     *
     * @param userId  Id владельца запроса.
     * @param userDto DTO объект содержащий информацию об обновлённом пользователе:
     * @return DTO объект обновлённого пользователя
     * @throws UserByIdNotFoundException если пользователь не найден.
     */
    @Override
    public UserResponseDto updateUserById(UUID userId, UserDto userDto) {
        User userUpdate = userRepository.findById(userId).
                orElseThrow(() -> new UserByIdNotFoundException(userId));

        if (userDto.getFirstName() != null) {
            userUpdate.setFirstName(userDto.getFirstName());
        }

        if (userDto.getLastName() != null) {
            userUpdate.setLastName(userDto.getLastName());
        }

        if (userDto.getEmail() != null) {
            userUpdate.setEmail(userDto.getEmail());
        }

        return UserMapper.toUserResponseDto(userRepository.save(userUpdate));
    }


    /**
     * Удаляет пользователя по его Id.
     * Так же проверят, является ли пользователем владельцем какой-либо доски,
     * если является то, производит передачу прав OWNER случайному пользователю с правами ADMIN в этой доске,
     * если пользователь с указанными правами не найден, то передаёт права случайному пользователю с правами EDITOR,
     * если пользователь с указанными правами не найден, то передаёт права случайному пользователю с правами USER,
     * если пользователь с указанными правами не найден, то удаляет доску и все связанные с ней сущности.
     *
     * @param userId Id владельца запроса.
     * @throws UserByIdNotFoundException  если пользователь не найден.
     * @throws BoardByIdNotFoundException если доска не найдена.
     */
    @Override
    public void deleteUserById(UUID userId) {
        List<Entitlement> entitlementsOwnerThisUser =
                entitlementRepository.findAllByUserIdAndEntitlement(userId, EntitlementEnum.OWNER);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserByIdNotFoundException(userId));

        if (entitlementsOwnerThisUser.isEmpty()) {
            userRepository.deleteById(userId);
            return;
        }

        nextEntitlement:
        for (Entitlement entitlementThisUser : entitlementsOwnerThisUser) {
            List<Entitlement> entitlementsThisBoard =
                    entitlementRepository.findAllByBoardId(entitlementThisUser.getBoard().getId());

            Board board = boardRepository.findById(entitlementThisUser.getBoard().getId())
                    .orElseThrow(() -> new BoardByIdNotFoundException(entitlementThisUser.getBoard().getId()));
            board.getUsers().remove(user);
            boardRepository.save(board);

            for (Entitlement entitlementAdminBoard : entitlementsThisBoard) {
                if (entitlementAdminBoard.getEntitlement().equals(EntitlementEnum.ADMIN)) {
                    entitlementAdminBoard.setEntitlement(EntitlementEnum.OWNER);
                    entitlementRepository.save(entitlementAdminBoard);
                    continue nextEntitlement;
                }
            }

            for (Entitlement entitlementEditorBoard : entitlementsThisBoard) {
                if (entitlementEditorBoard.getEntitlement().equals(EntitlementEnum.EDITOR)) {
                    entitlementEditorBoard.setEntitlement(EntitlementEnum.OWNER);
                    entitlementRepository.save(entitlementEditorBoard);
                    continue nextEntitlement;
                }
            }

            for (Entitlement entitlementUserBoard : entitlementsThisBoard) {
                if (entitlementUserBoard.getEntitlement().equals(EntitlementEnum.USER)) {
                    entitlementUserBoard.setEntitlement(EntitlementEnum.OWNER);
                    entitlementRepository.save(entitlementUserBoard);
                    continue nextEntitlement;
                }
            }

            boardService.deleteBoardById(userId, entitlementThisUser.getBoard().getId());
        }
        entitlementRepository.deleteAllByUserId(userId);
        commentRepository.deleteAllByAuthorId(userId);
        userRepository.deleteById(userId);
    }
}

