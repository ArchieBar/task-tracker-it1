package ru.itone.model.user;

import ru.itone.model.tasks.epic.Epic;
import ru.itone.model.user.dto.UserResponseDto;

import java.util.Set;

public class UserMapper {
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
            String email = user.getEmail();
            userResponseDto.setEmail(email);
        }

        if (user.getEpics() != null) {
            Set<Epic> epics = user.getEpics();
            userResponseDto.setEpics(epics);
        }

        return userResponseDto;
    }

    //FIXME Убрать если не использую
//    public static List<UserResponseDto> toUserResponseDtoList(List<User> userList) {
//        return userList.stream()
//                .map(UserMapper::toUserResponseDto)
//                .collect(Collectors.toList());
//    }
}
