package ru.itone.service.epic;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.itone.exception.board.BoardByIdNotFoundException;
import ru.itone.exception.epic.EpicByIdNotFoundException;
import ru.itone.exception.epic.comment.CommentByIdNotFoundException;
import ru.itone.exception.user.UserAccessDeniedException;
import ru.itone.exception.user.UserByIdNotFoundException;
import ru.itone.exception.user.UserNotFoundInEpicException;
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
     * @param boardId Id доски.
     * @return Список DTO объектов эпиков.
     * @throws BoardByIdNotFoundException В случае если доска не найдена.
     */
    @Override
    public List<EpicResponseDto> findEpicsByBoardId(UUID boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardByIdNotFoundException(boardId));

        Set<Epic> epics = board.getEpics();

        return EpicMapper.toEpicResponseDtoList(epics);
    }

    /**
     * Находит Эпик по Id.
     *
     * @param epicId Id эпика.
     * @return DTO объект эпика.
     * @throws EpicByIdNotFoundException В случае если эпик не найден.
     */
    @Override
    public EpicResponseDto findEpicById(UUID epicId) {
        Epic epic = epicRepository.findById(epicId)
                .orElseThrow(() -> new EpicByIdNotFoundException(epicId));

        return EpicMapper.toEpicResponseDto(epic);
    }

    /**
     * Создаёт новую сущность на основе DTO объекта.
     * Создать эпик могут все пользователи которые имеют права OWNER, ADMIN или EDITOR.
     *
     * @param userId  Id владельца запроса.
     * @param epicDto DTO объект содержащий информацию о новом эпике.
     * @param boardId Id доски задач.
     * @return DTO объект нового эпика.
     * @throws BoardByIdNotFoundException                    если доска не найдена.
     * @throws UserByIdNotFoundException                     если пользователь не найден.
     * @throws UserRightsByUserIdAndBoardIdNotFoundException если права владельца запроса не найдены в доске.
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
                .orElseThrow(() -> new BoardByIdNotFoundException(boardId));

        Epic epic = new Epic(epicDto, board);
        epic = epicRepository.save(epic);

        board.addEpic(epic);
        boardRepository.save(board);

        return EpicMapper.toEpicResponseDto(epic);
    }

    /**
     * Создаёт новый комментарий в эпике.
     * Комментарии могут создать только участники доски.
     *
     * @param userId     Id владельца запроса.
     * @param epicId     Id эпика в котором хотят оставить комментарий.
     * @param commentDto DTO объект содержащий информацию о новом комментарии.
     * @return DTO объект нового комментария.
     * @throws UserByIdNotFoundException                     если пользователь не найден.
     * @throws EpicByIdNotFoundException                     если эпик не найден.
     * @throws UserRightsByUserIdAndBoardIdNotFoundException если права пользователя в доске не найдены.
     */
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
     * Обновляет эпик на основе DTO объекта.
     * Обновлять эпики могут пользователи с правами OWNER, ADMIN или EDITOR.
     *
     * @param userId  Id владельца запроса.
     * @param epicId  Id эпика для обновления.
     * @param epicDto DTO объект содержащий информацию об обновляемом эпике.
     * @return DTO объект обновлённой сущности эпика.
     * @throws UserByIdNotFoundException                     если пользователь не найден.
     * @throws EpicByIdNotFoundException                     В случае если сущность не найдена.
     * @throws UserRightsByUserIdAndBoardIdNotFoundException если права пользователя в доске не найдены.
     * @throws UserAccessDeniedException                     если пользователю отказано в доступе.
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

    /**
     * Позволяет пользователю взять эпик для его выполнения.
     * Брать эпики могут только участники доски.
     *
     * @param userId Id владельца запроса.
     * @param epicId Id эпика.
     * @throws EpicByIdNotFoundException                     если эпик не найден.
     * @throws UserByIdNotFoundException                     если пользователь не найден.
     * @throws UserRightsByUserIdAndBoardIdNotFoundException если права пользователя не найдены.
     */
    @Override
    public void takeEpic(UUID userId, UUID epicId) {
        Epic epic = epicRepository.findById(epicId)
                .orElseThrow(() -> new EpicByIdNotFoundException(epicId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserByIdNotFoundException(userId));

        entitlementRepository.findByUserIdAndBoardId(userId, epic.getBoard().getId())
                .orElseThrow(() -> new UserRightsByUserIdAndBoardIdNotFoundException(userId, epic.getBoard().getId()));

        epic.addUser(user);
        epicRepository.save(epic);
    }

    /**
     * Позволяет пользователю отказаться от выполнения эпика.
     *
     * @param userId Id владельца запроса.
     * @param epicId Id эпика.
     * @throws EpicByIdNotFoundException   если эпик не найден.
     * @throws UserByIdNotFoundException   если пользователь не найден.
     * @throws UserNotFoundInEpicException если пользователь не найден в числе участников эпика.
     */
    @Override
    public void refuseEpic(UUID userId, UUID epicId) {
        Epic epic = epicRepository.findById(epicId)
                .orElseThrow(() -> new EpicByIdNotFoundException(epicId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserByIdNotFoundException(userId));

        Set<User> users = epic.getUsers();

        if (users.contains(user)) {
            users.remove(user);
            epic.setUsers(users);
            epicRepository.save(epic);
        } else {
            throw new UserNotFoundInEpicException(epicId, userId);
        }
    }

    /**
     * Обновляет комментарий на основе dto объекта.
     * Обновить комментарий может только автор комментария.
     *
     * @param userId     Id владельца запроса.
     * @param commentId  Id комментария.
     * @param commentDto DTO объект обновляемого комментария.
     * @return DTO объект обновлённого комментария.
     * @throws CommentByIdNotFoundException если комментарий не найден.
     * @throws UserAccessDeniedException    если пользователю отказано в доступе.
     */
    @Override
    public CommentResponseDto updateCommentById(UUID userId, UUID commentId, CommentDto commentDto) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentByIdNotFoundException(commentId));

        if (!comment.getAuthor().getId().equals(userId)) {
            throw new UserAccessDeniedException(userId);
        }

        comment.setText(commentDto.getText());

        return CommentMapper.toCommentResponseDto(commentRepository.save(comment));
    }

    /**
     * Удаляет эпик по Id. Также удаляет все связанные сущности задач.
     * Удалить эпик могут пользователи с правами в этой доске OWNER, ADMIN или EDITOR.
     *
     * @param userId  Id владельца запроса.
     * @param boardId Id доски.
     * @param epicId  Id эпика.
     * @throws UserRightsByUserIdAndBoardIdNotFoundException если права пользователя в доске не найдены.
     * @throws UserAccessDeniedException                     если пользователю отказано в доступе.
     * @throws EpicByIdNotFoundException                     если эпик не найден.
     * @throws BoardByIdNotFoundException                    если доска не найдена.
     */
    @Override
    public void deleteEpicById(UUID userId, UUID boardId, UUID epicId) {
        Entitlement entitlement = entitlementRepository.findByUserIdAndBoardId(userId, boardId)
                .orElseThrow(() -> new UserRightsByUserIdAndBoardIdNotFoundException(userId, boardId));

        if (entitlement.getEntitlement().equals(EntitlementEnum.USER)) {
            throw new UserAccessDeniedException(userId);
        }

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardByIdNotFoundException(boardId));

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

    /**
     * Удаляет комментарий.
     * Удалить комментарий могут либо авторы комментария, либо пользователи с права в доске OWNER или ADMIN.
     *
     * @param userId    Id владельца запроса.
     * @param epicId    Id эпика.
     * @param commentId Id комментария.
     * @throws EpicByIdNotFoundException                     если эпик не найден.
     * @throws UserRightsByUserIdAndBoardIdNotFoundException если права пользователя в доске не найдены.
     * @throws CommentByIdNotFoundException                  если комментарий не найден.
     * @throws UserAccessDeniedException                     если пользователю отказано в доступе.
     */
    @Override
    public void deleteCommentById(UUID userId, UUID epicId, UUID commentId) {
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

        if (!comment.getAuthor().getId().equals(userId) &&
                (entitlementEnum.equals(EntitlementEnum.USER) || entitlementEnum.equals(EntitlementEnum.EDITOR))) {
            throw new UserAccessDeniedException(userId);
        }

        epic.getActivity().remove(comment);
        epicRepository.save(epic);

        commentRepository.deleteById(commentId);
    }
}
