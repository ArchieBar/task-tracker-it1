package ru.itone.model.user;

import ru.itone.model.user.dto.UserFullNameAndEmailDto;
import ru.itone.model.user.dto.UserResponseDto;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class UserMapper {
    public static UserResponseDto toUserResponseDto(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getFirstName() + " " + user.getLastName(),
                user.getEmail()
        );
    }

    public static List<UserResponseDto> toUserResponseDtoList(Set<User> users) {
        return users.stream()
                .map(UserMapper::toUserResponseDto)
                .collect(Collectors.toList());
    }

    public static UserFullNameAndEmailDto toUserFullNameAndEmailDto(User user) {
        return new UserFullNameAndEmailDto(
                user.getFirstName() + " " + user.getLastName(),
                user.getEmail()
        );
    }

    public static List<UserFullNameAndEmailDto> toUserFullNameAndEmailDtoList(Set<User> users) {
        return users.stream()
                .map(UserMapper::toUserFullNameAndEmailDto)
                .collect(Collectors.toList());
    }
}
