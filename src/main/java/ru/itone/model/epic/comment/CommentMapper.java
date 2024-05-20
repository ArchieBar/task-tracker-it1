package ru.itone.model.epic.comment;

import ru.itone.model.epic.comment.dto.CommentResponseDto;
import ru.itone.model.user.UserMapper;
import ru.itone.model.user.dto.UserFullNameAndEmailDto;

import java.util.List;
import java.util.stream.Collectors;

public class CommentMapper {
    public static CommentResponseDto toCommentResponseDto(Comment comment) {
        CommentResponseDto commentResponseDto = new CommentResponseDto();

        commentResponseDto.setId(comment.getId());

        if (comment.getText() != null) {
            commentResponseDto.setText(comment.getText());
        }

        commentResponseDto.setCreatedTime(comment.getCreatedTime());

        UserFullNameAndEmailDto user = UserMapper.toUserFullNameAndEmailDto(comment.getAuthor());
        commentResponseDto.setAuthor(user);

        return commentResponseDto;
    }

    public static List<CommentResponseDto> toCommentResponseDtoList(List<Comment> comments) {
        return comments.stream()
                .map(CommentMapper::toCommentResponseDto)
                .collect(Collectors.toList());
    }
}
