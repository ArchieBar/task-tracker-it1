package ru.itone.service.user;

import ru.itone.model.user.dto.LoginFormDto;
import ru.itone.model.user.dto.RegisterFormDto;
import ru.itone.model.user.dto.UserDto;
import ru.itone.model.user.dto.UserResponseDto;

import java.util.UUID;

public interface UserService {
    UserResponseDto findUserById(UUID userId, UUID searchUserId);

    UserResponseDto registerUser(RegisterFormDto dto);

    UserResponseDto login(LoginFormDto loginFormDto);

    UserResponseDto logout(UUID userId);

    UserResponseDto updateUserById(UUID userId, UserDto userDto);

    void deleteUserById(UUID userId);
}
