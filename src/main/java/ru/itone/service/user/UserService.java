package ru.itone.service.user;

import ru.itone.model.board.dto.BoardResponseDto;
import ru.itone.model.user.dto.LoginFormDto;
import ru.itone.model.user.dto.RegisterFormDto;
import ru.itone.model.user.dto.UserDto;
import ru.itone.model.user.dto.UserResponseDto;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserResponseDto findUserById(UUID userId);

    List<BoardResponseDto> findInviteByUser(UUID userId);

    UserResponseDto registerUser(RegisterFormDto dto);

    UserResponseDto login(LoginFormDto loginFormDto);

    UserResponseDto logout(UUID userId);

    void confirmInvite(UUID userId, UUID boardId);

    UserResponseDto updateUserById(UUID userId, UserDto userDto);

    void deleteUserById(UUID userId);
}
