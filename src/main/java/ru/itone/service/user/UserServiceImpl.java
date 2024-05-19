package ru.itone.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.itone.exception.board.BoardNotFoundByIdException;
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
import ru.itone.repository.BoardRepository;
import ru.itone.repository.EntitlementRepository;
import ru.itone.repository.InviteRepository;
import ru.itone.repository.UserRepository;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final InviteRepository inviteRepository;
    private final BoardRepository boardRepository;
    private final EntitlementRepository entitlementRepository;

    /**
     * Находит пользователя по его идентификатору.
     *
     * @param userId идентификатор пользователя, которого нужно найти.
     * @return UserResponseDto объект, содержащий информацию о пользователе:
     * {UUID id, String fullName, String email, Set epics}
     * @throws UserByIdNotFoundException если пользователь с указанным идентификатором не найден.
     *                                   Сообщение: "Пользователь с ID: {0} не найден.". HTTP Code: 404
     */
    @Override
    public UserResponseDto findUserById(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserByIdNotFoundException(userId));

        return UserMapper.toUserResponseDto(user);
    }

    @Override
    public List<BoardResponseDto> findInviteByUser(UUID userId) {
        List<Invite> invitations = inviteRepository.findAllByUserIdAndConfirmed(userId, false);

        return invitations.stream()
                .map(i -> BoardMapper.toBoardResponseDto(i.getBoard()))
                .collect(Collectors.toList());
    }


    /**
     * Создаёт нового пользователя на основе DTO объекта. Id генерируется автоматически на уровне бд.
     *
     * @param dto DTO объект содержащий информацию о новом пользователе:
     *            {String firstName, String lastName, String email, String password}.
     * @return UserResponseDto объект, содержащий информацию о пользователе:
     * {UUID id, String fullName, String email, Set epics}
     */
    @Override
    public UserResponseDto registerUser(RegisterFormDto dto) {
        User user = new User(dto);

        userRepository.save(user);

        return UserMapper.toUserResponseDto(user);
    }

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

    @Override
    public UserResponseDto logout(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserByIdNotFoundException(userId));

        user.setLogon(false);
        userRepository.save(user);

        return UserMapper.toUserResponseDto(user);
    }

    @Override
    public void confirmInvite(UUID userId, UUID boardId) {
        Invite invite = inviteRepository.findByUserIdAndBoardId(userId, boardId)
                .orElseThrow(() -> new InviteByUserIdAndBoardIdNotFoundException(userId, boardId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserByIdNotFoundException(userId));

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardNotFoundByIdException(boardId));

        invite.setConfirmed(true);
        inviteRepository.save(invite);

        board.addUser(user);
        boardRepository.save(board);
    }


    /**
     * Обновляет существующего пользователя по идентификатору,
     * в случае, если сущность не найдена - выбрасывает исключение.
     *
     * @param userId  идентификатор пользователя, которого нужно обновить.
     * @param userDto DTO объект содержащий информацию об обновлённом пользователе:
     *                {String firstName, String lastName, String email}, поля могут быть равны 'null',
     *                обновляются только те поля у основной сущности, которые у DTO объекта прошли валидацию
     *                и не равны 'null'.
     * @return UserResponseDto объект, содержащий информацию о пользователе:
     * {UUID id, String fullName, String email, Set epics}
     * @throws UserByIdNotFoundException если пользователь с указанным идентификатором не найден.
     *                                   Сообщение: "Пользователь с ID: {0} не найден.". HTTP Code: 40
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
     * Удаляет пользователя по его идентификатор
     *
     * @param userId идентификатор пользователя, которого нужно удалить.
     */
    @Override
    public void deleteUserById(UUID userId) {
        List<Entitlement> entitlementsOwnerThisUser =
                entitlementRepository.findAllByUserIdAndEntitlement(userId, EntitlementEnum.OWNER);

        if (entitlementsOwnerThisUser.isEmpty()) {
            List<Entitlement> entitlements = entitlementRepository.findAllByUserId(userId);

            entitlementRepository.deleteAll(entitlements);
            userRepository.deleteById(userId);
            return;
        }

        nextEntitlement:
        for (Entitlement entitlementThisUser : entitlementsOwnerThisUser) {
            List<Entitlement> entitlementsThisBoard =
                    entitlementRepository.findAllByBoardId(entitlementThisUser.getBoard().getId());
            entitlementsThisBoard.remove(entitlementThisUser);

            for (Entitlement entitlementAdminBoard : entitlementsThisBoard) {
                if (entitlementAdminBoard.getEntitlement().equals(EntitlementEnum.ADMIN)) {
                    entitlementAdminBoard.setEntitlement(EntitlementEnum.OWNER);
                    entitlementRepository.save(entitlementAdminBoard);
                    continue nextEntitlement;
                } else {
                    entitlementsThisBoard.remove(entitlementAdminBoard);

                    if (entitlementsThisBoard.isEmpty()) {
                        boardRepository.deleteById(entitlementThisUser.getBoard().getId());
                        continue nextEntitlement;
                    }
                }
            }

            for (Entitlement entitlementEditorBoard : entitlementsThisBoard) {
                if (entitlementEditorBoard.getEntitlement().equals(EntitlementEnum.EDITOR)) {
                    entitlementEditorBoard.setEntitlement(EntitlementEnum.OWNER);
                    entitlementRepository.save(entitlementEditorBoard);
                    continue nextEntitlement;
                } else {
                    entitlementsThisBoard.remove(entitlementEditorBoard);

                    if (entitlementsThisBoard.isEmpty()) {
                        boardRepository.deleteById(entitlementThisUser.getBoard().getId());
                        continue nextEntitlement;
                    }
                }
            }

            for (Entitlement entitlementUserBoard : entitlementsThisBoard) {
                if (entitlementUserBoard.getEntitlement().equals(EntitlementEnum.USER)) {
                    entitlementUserBoard.setEntitlement(EntitlementEnum.OWNER);
                    entitlementRepository.save(entitlementUserBoard);
                    continue nextEntitlement;
                } else {
                    entitlementsThisBoard.remove(entitlementUserBoard);
                }
            }
            boardRepository.deleteById(entitlementThisUser.getBoard().getId());
        }
        userRepository.deleteById(userId);
    }
}

