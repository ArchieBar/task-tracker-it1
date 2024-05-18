package ru.itone.model.user;

import ru.itone.model.epic.EpicMapper;
import ru.itone.model.epic.dto.EpicResponseDto;
import ru.itone.model.user.dto.UserResponseDto;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class UserMapper {
    //TODO
    // Пробросить NPE?
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
            List<EpicResponseDto> epics = EpicMapper.toEpicResponseDtoList(user.getEpics());
            userResponseDto.setEpics(epics);
        }

        return userResponseDto;
    }

    public static List<UserResponseDto> toUserResponseDtoList(Set<User> users) {
        return users.stream()
                .map(UserMapper::toUserResponseDto)
                .collect(Collectors.toList());
    }
}
