package ru.itone.model.user;

import ru.itone.model.tasks.epic.Epic;
import ru.itone.model.user.dto.UserResponseDto;

import java.util.List;
import java.util.stream.Collectors;

public class UserMapper {
    //TODO
    // Добавить проброс исключения в случае NPE у полей через блок else
    public static UserResponseDto toUserResponseDto(User user) {
        UserResponseDto userResponseDto = new UserResponseDto();

        if (user.getId() != null) {
            userResponseDto.setId(user.getId());
        }

        if (user.getFirstName() != null && user.getLastName() != null) {
            String firstName = user.getFirstName();
            String lastName = user.getLastName();
            userResponseDto.setFullName(firstName, lastName);
        }

        if (user.getEmail() != null) {
            userResponseDto.setEmail(user.getEmail());
        }

        if (user.getEpics() != null) {
            userResponseDto.setEpics(user.getEpics());
        }

        return userResponseDto;
    }

    public static List<UserResponseDto> toUserResponseDtoList(List<User> userList) {
        return userList.stream()
                .map(UserMapper::toUserResponseDto)
                .collect(Collectors.toList());
    }
}
