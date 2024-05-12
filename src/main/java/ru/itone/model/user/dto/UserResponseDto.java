package ru.itone.model.user.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.itone.model.epic.Epic;
import ru.itone.model.epic.dto.EpicResponseDto;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
public class UserResponseDto {
    private UUID id;
    private String fullName;
    private String email;
    private List<EpicResponseDto> epics;

    public UserResponseDto(UUID id,
                           String firstName,
                           String lastName,
                           String email,
                           List<EpicResponseDto> epics) {
        this.id = id;
        this.fullName = String.format("%s %s", firstName, lastName);
        this.email = email;
        this.epics = epics;
    }

    public void setFullName(String firstName, String lastName) {
        this.fullName = String.format("%s %s", firstName, lastName);
    }
}
