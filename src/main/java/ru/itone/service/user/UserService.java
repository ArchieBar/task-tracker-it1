package ru.itone.service.user;

import ru.itone.model.user.dto.UserDto;
import ru.itone.model.user.dto.UserResponseDto;

import java.util.UUID;

public interface UserService {
    UserResponseDto findUserById(UUID userId);

    UserResponseDto createUser(UserDto userDto);

    UserResponseDto updateUserById(UUID userId, UserDto userDto);

    void deleteUserById(UUID userId);
}
