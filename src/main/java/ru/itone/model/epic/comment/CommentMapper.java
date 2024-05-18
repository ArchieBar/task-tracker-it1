package ru.itone.model.epic.comment;

import ru.itone.model.epic.comment.dto.CommentResponseDto;
import ru.itone.model.user.UserMapper;
import ru.itone.model.user.dto.UserResponseDto;

import java.util.List;
import java.util.stream.Collectors;

public class CommentMapper {
    //TODO
    // Пробросить NPE
    public static CommentResponseDto toCommentResponseDto(Comment comment) {
        CommentResponseDto commentResponseDto = new CommentResponseDto();

        if (comment.getId() != null) {
            commentResponseDto.setId(comment.getId());
        }

        if (comment.getText() != null) {
            commentResponseDto.setText(comment.getText());
        }

        //TODO
        // Выводить в формате: дд-мм-гг чч-ММ?
        if (comment.getCreatedTime() != null) {
            commentResponseDto.setCreatedTime(comment.getCreatedTime());
        }

        if (comment.getAuthor() != null) {
            UserResponseDto user = UserMapper.toUserResponseDto(comment.getAuthor());
            commentResponseDto.setAuthor(user);
        }

        return commentResponseDto;
    }

    public static List<CommentResponseDto> toCommentResponseDtoList(List<Comment> comments) {
        return comments.stream()
                .map(CommentMapper::toCommentResponseDto)
                .collect(Collectors.toList());
    }
}
