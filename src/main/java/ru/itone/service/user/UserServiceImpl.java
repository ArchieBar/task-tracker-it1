package ru.itone.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.itone.exception.user.UserByEmailNotFoundException;
import ru.itone.exception.user.UserByIdNotFoundException;
import ru.itone.exception.user.UserInvalidPassword;
import ru.itone.exception.user.UserLoginHasBeenCompletedException;
import ru.itone.model.user.User;
import ru.itone.model.user.UserMapper;
import ru.itone.model.user.dto.LoginFormDto;
import ru.itone.model.user.dto.RegisterFormDto;
import ru.itone.model.user.dto.UserDto;
import ru.itone.model.user.dto.UserResponseDto;
import ru.itone.repository.UserRepository;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

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
    public UserResponseDto findUserById(UUID userId, UUID searchUserId) {
        Optional<User> userOptional = userRepository.findById(searchUserId);

        if (userOptional.isEmpty()) {
            throw new UserByIdNotFoundException(searchUserId);
        }

        return UserMapper.toUserResponseDto(userOptional.get());
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
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            throw new UserByIdNotFoundException(userId);
        }

        User userUpdate = userOptional.get();

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
        userRepository.deleteById(userId);
    }
}
