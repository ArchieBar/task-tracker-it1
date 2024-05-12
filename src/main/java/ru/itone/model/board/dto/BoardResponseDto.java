package ru.itone.model.board.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.itone.model.epic.dto.EpicResponseDto;
import ru.itone.model.user.dto.UserResponseDto;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoardResponseDto {
    private UUID id;
    private String name;
    private List<EpicResponseDto> epics;
    private List<UserResponseDto> users;
}
