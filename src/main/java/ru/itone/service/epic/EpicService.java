package ru.itone.service.epic;

import org.springframework.data.domain.Pageable;
import ru.itone.model.epic.comment.dto.CommentDto;
import ru.itone.model.epic.comment.dto.CommentResponseDto;
import ru.itone.model.epic.dto.EpicDto;
import ru.itone.model.epic.dto.EpicResponseDto;

import java.util.List;
import java.util.UUID;

public interface EpicService {
    List<EpicResponseDto> findEpicsByBoardId(UUID boarId);

    EpicResponseDto findEpicById(UUID epicId);

    EpicResponseDto createEpic(UUID userId,
                               UUID boardId,
                               EpicDto epicDto);

    CommentResponseDto createCommentByEpicId(UUID userId,
                                             UUID epicId,
                                             CommentDto commentDto);

    EpicResponseDto updateEpicById(UUID userId,
                                   UUID epicId,
                                   EpicDto epicDto);

    CommentResponseDto updateCommentById(UUID userId,
                                         UUID commentId,
                                         CommentDto commentDto);

    void deleteEpicById(UUID userId,
                        UUID boardId,
                        UUID epicId);

    void deleteCommentById(UUID userId,
                           UUID epicId,
                           UUID commentId);
}
