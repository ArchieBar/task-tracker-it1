package ru.itone.service.epic;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.itone.exception.board.BoardNotFoundByIdException;
import ru.itone.exception.epic.EpicByIdNotFoundException;
import ru.itone.exception.epic.comment.CommentByIdNotFoundException;
import ru.itone.exception.user.UserAccessDeniedException;
import ru.itone.exception.user.UserByIdNotFoundException;
import ru.itone.exception.user.UserRightsByUserIdAndBoardIdNotFoundException;
import ru.itone.model.board.Board;
import ru.itone.model.epic.Epic;
import ru.itone.model.epic.EpicMapper;
import ru.itone.model.epic.comment.Comment;
import ru.itone.model.epic.comment.CommentMapper;
import ru.itone.model.epic.comment.dto.CommentDto;
import ru.itone.model.epic.comment.dto.CommentResponseDto;
import ru.itone.model.epic.dto.EpicDto;
import ru.itone.model.epic.dto.EpicResponseDto;
import ru.itone.model.task.Task;
import ru.itone.model.user.Entitlement;
import ru.itone.model.user.EntitlementEnum;
import ru.itone.model.user.User;
import ru.itone.repository.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EpicServiceImpl implements EpicService {
    private final UserRepository userRepository;
    private final EntitlementRepository entitlementRepository;
    private final BoardRepository boardRepository;
    private final EpicRepository epicRepository;
    private final TaskRepository taskRepository;
    private final CommentRepository commentRepository;

    /**
     * Возвращает все Эпики одной доски по Id постранично используя Pageable.
     *
     * @param boardId Id доски к которой принадлежит эпик.
     * @return Список DTO объектов EpicResponseDto сущностей Epic.
     * @throws BoardNotFoundByIdException В случае если сущность не найдена.
     *                                    Сообщение: "Доска задач с ID: '%s' не найдена.". Обработка в ErrorHandler.
     */
    @Override
    public List<EpicResponseDto> findEpicsByBoardId(UUID boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardNotFoundByIdException(boardId));

        Set<Epic> epics = board.getEpics();

        return EpicMapper.toEpicResponseDtoList(epics);
    }

    /**
     * Находит Эпик по UUID, если сущность не найдена пробрасывает исключение.
     *
     * @param epicId Id сущности в формате UUID.
     * @return DTO объект EpicResponseDto сущности Epic.
     * @throws EpicByIdNotFoundException В случае если сущность не найдена.
     *                                   Сообщение: "Эпик с ID: '%s' не найден.". Обработка в ErrorHandler.
     */
    @Override
    public EpicResponseDto findEpicById(UUID epicId) {
        Epic epic = epicRepository.findById(epicId)
                .orElseThrow(() -> new EpicByIdNotFoundException(epicId));

        return EpicMapper.toEpicResponseDto(epic);
    }

    /**
     * Создаёт новую сущность на основе DTO объекта. Id генерируется на уровне бд.
     *
     * @param epicDto DTO объект содержащий информацию о новом Эпике.
     * @param boardId Id доски задач.
     * @return DTO объект EpicResponseDto новой сущности Epic.
     * @throws BoardNotFoundByIdException В случае если сущность не найдена.
     *                                    Сообщение: "Доска задач с ID: '%s' не найдена.". Обработка в ErrorHandler.
     */
    @Override
    public EpicResponseDto createEpic(UUID userId, UUID boardId, EpicDto epicDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserByIdNotFoundException(userId));

        Entitlement entitlement = user.getEntitlements().stream()
                .filter(e -> e.getBoard().getId().equals(boardId))
                .findFirst()
                .orElseThrow(() -> new UserRightsByUserIdAndBoardIdNotFoundException(userId, boardId));

        if (entitlement.getEntitlement().equals(EntitlementEnum.USER)) {
            throw new UserAccessDeniedException(userId);
        }

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardNotFoundByIdException(boardId));

        Epic epic = new Epic(epicDto, board, user);
        epic = epicRepository.save(epic);

        board.addEpic(epic);
        boardRepository.save(board);

        return EpicMapper.toEpicResponseDto(epic);
    }

    //TODO
    // Добавить проверку прав доступа
    // Создавать комментарии могут только участники эпика
    @Override
    public CommentResponseDto createCommentByEpicId(UUID userId, UUID epicId, CommentDto commentDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserByIdNotFoundException(userId));

        Epic epic = epicRepository.findById(epicId)
                .orElseThrow(() -> new EpicByIdNotFoundException(epicId));

        UUID boardId = epic.getBoard().getId();

        user.getEntitlements().stream()
                .filter(e -> e.getBoard().getId().equals(boardId))
                .findFirst()
                .orElseThrow(() -> new UserRightsByUserIdAndBoardIdNotFoundException(userId, boardId));

        Comment comment = new Comment(commentDto, user, epic);
        comment = commentRepository.save(comment);

        epic.addComment(comment);
        epicRepository.save(epic);

        return CommentMapper.toCommentResponseDto(comment);
    }

    /**
     * Обновляет Epic на основе DTO объекта.
     *
     * @param epicId  Id сущности в формате UUID.
     * @param epicDto DTO объект содержащий информацию об обновлённом Эпике.
     * @return DTO объект EpicResponseDto обновлённой сущности Epic.
     * @throws EpicByIdNotFoundException В случае если сущность не найдена.
     *                                   Сообщение: "Эпик с ID: '%s' не найден.". Обработка в ErrorHandler.
     */
    @Override
    public EpicResponseDto updateEpicById(UUID userId, UUID epicId, EpicDto epicDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserByIdNotFoundException(userId));

        Epic epic = epicRepository.findById(epicId)
                .orElseThrow(() -> new EpicByIdNotFoundException(epicId));

        UUID boardId = epic.getBoard().getId();

        Entitlement entitlement = user.getEntitlements().stream()
                .filter(e -> e.getBoard().getId().equals(boardId))
                .findFirst()
                .orElseThrow(() -> new UserRightsByUserIdAndBoardIdNotFoundException(userId, boardId));

        if (entitlement.getEntitlement().equals(EntitlementEnum.USER)) {
            throw new UserAccessDeniedException(userId);
        }

        if (epicDto.getName() != null) {
            epic.setName(epicDto.getName());
        }

        if (epicDto.getDescription() != null) {
            epic.setDescription(epicDto.getDescription());
        }

        return EpicMapper.toEpicResponseDto(epicRepository.save(epic));
    }

    @Override
    public CommentResponseDto updateCommentById(UUID userId, UUID commentId, CommentDto commentDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserByIdNotFoundException(userId));

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentByIdNotFoundException(commentId));

        if (comment.getAuthor() != user) {
            throw new UserAccessDeniedException(userId);
        }

        if (commentDto != null) {
            comment.setText(commentDto.getText());
        }

        return CommentMapper.toCommentResponseDto(commentRepository.save(comment));
    }

    /**
     * Удаляет сущность по Id. Также удаляет все связанные сущности задач.
     *
     * @param epicId Id в формате UUID.
     * @throws EpicByIdNotFoundException  В случае если сущность не найдена.
     *                                    Сообщение: "Эпик с ID: '%s' не найден.". Обработка в ErrorHandler.
     * @throws BoardNotFoundByIdException В случае если сущность не найдена.
     *                                    Сообщение: "Доска задач с ID: '%s' не найдена.". Обработка в ErrorHandler.
     */

    //TODO
    // это можно сделать через каскадные операции,
    // изучить и применить
    @Override
    public void deleteEpicById(UUID userId, UUID boardId, UUID epicId) {
        Entitlement entitlement = entitlementRepository.findByUserIdAndBoardId(userId, boardId)
                .orElseThrow(() -> new UserRightsByUserIdAndBoardIdNotFoundException(userId, boardId));

        if (entitlement.getEntitlement().equals(EntitlementEnum.USER)) {
            throw new UserAccessDeniedException(userId);
        }

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardNotFoundByIdException(boardId));

        Epic epic = board.getEpics().stream()
                .filter(e -> e.getId().equals(epicId))
                .findFirst()
                .orElseThrow(() -> new EpicByIdNotFoundException(epicId));

        Set<Task> tasks = epic.getTasks();

        board.getEpics().remove(epic);
        boardRepository.save(board);

        taskRepository.deleteAll(tasks);
        epicRepository.deleteById(epicId);
    }

    @Override
    public void deleteCommentById(UUID userId, UUID epicId, UUID commentId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserByIdNotFoundException(userId));

        Epic epic = epicRepository.findById(epicId)
                .orElseThrow(() -> new EpicByIdNotFoundException(epicId));

        UUID boardId = epic.getBoard().getId();

        Entitlement entitlement = entitlementRepository.findByUserIdAndBoardId(userId, boardId)
                .orElseThrow(() -> new UserRightsByUserIdAndBoardIdNotFoundException(userId, boardId));

        Comment comment = epic.getActivity().stream()
                .filter(c -> c.getId().equals(commentId))
                .findFirst()
                .orElseThrow(() -> new CommentByIdNotFoundException(commentId));

        EntitlementEnum entitlementEnum = entitlement.getEntitlement();


        if (comment.getAuthor().getId() != user.getId() &&
                (entitlementEnum.equals(EntitlementEnum.USER) || entitlementEnum.equals(EntitlementEnum.EDITOR))) {
            throw new UserAccessDeniedException(userId);
        }

        epic.getActivity().remove(comment);
        epicRepository.save(epic);

        commentRepository.deleteById(commentId);
    }
}
