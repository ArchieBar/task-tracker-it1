package ru.itone.model.board;

import ru.itone.model.board.dto.BoardResponseDto;
import ru.itone.model.epic.EpicMapper;
import ru.itone.model.epic.dto.EpicResponseDto;
import ru.itone.model.user.UserMapper;
import ru.itone.model.user.dto.UserResponseDto;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class BoardMapper {
    public static BoardResponseDto toBoardResponseDto(Board board) {
        BoardResponseDto boardResponseDto = new BoardResponseDto();

        boardResponseDto.setId(board.getId());

        boardResponseDto.setName(board.getName());

        if (board.getEpics() != null) {
            List<EpicResponseDto> epics = EpicMapper.toEpicResponseDtoList(board.getEpics());
            boardResponseDto.setEpics(epics);
        }

        if (board.getUsers() != null) {
            List<UserResponseDto> users = UserMapper.toUserResponseDtoList(board.getUsers());
            boardResponseDto.setUsers(users);
        }

        return boardResponseDto;
    }

    public static List<BoardResponseDto> toBoardResponseDtoList(Set<Board> boards) {
        return boards.stream()
                .map(BoardMapper::toBoardResponseDto)
                .collect(Collectors.toList());
    }
}
