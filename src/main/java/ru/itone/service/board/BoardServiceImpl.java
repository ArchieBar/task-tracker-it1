package ru.itone.service.board;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.itone.exception.board.BoardByIdNotFoundException;
import ru.itone.exception.user.UserAccessDeniedException;
import ru.itone.exception.user.UserByIdNotFoundException;
import ru.itone.exception.user.UserRightsByUserIdAndBoardIdNotFoundException;
import ru.itone.model.board.Board;
import ru.itone.model.board.BoardMapper;
import ru.itone.model.board.dto.BoardDto;
import ru.itone.model.board.dto.BoardResponseDto;
import ru.itone.model.board.invite.Invite;
import ru.itone.model.epic.Epic;
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
public class BoardServiceImpl implements BoardService {
    private final UserRepository userRepository;
    private final EntitlementRepository entitlementRepository;
    private final BoardRepository boardRepository;
    private final EpicRepository epicRepository;
    private final TaskRepository taskRepository;
    private final InviteRepository inviteRepository;

    /**
     * Возвращает все Доски задач постранично используя Pageable.
     *
     * @param pageable Формируется в контроллере исходя из параметров запроса.
     * @return Список DTO объектов BoardResponseDto сущностей Board.
     */
    @Override
    public List<BoardResponseDto> findBoards(Pageable pageable) {
        Set<Board> boards = boardRepository.findAll(pageable).toSet();

        return BoardMapper.toBoardResponseDtoList(boards);
    }

    /**
     * Находит Доску по UUID, если сущность не найдена пробрасывает исключение.
     *
     * @param boardId Id сущности в формате UUID.
     * @return DTO объект BoardResponseDto сущности Board.
     * @throws BoardByIdNotFoundException В случае если сущность не найдена.
     */
    @Override
    public BoardResponseDto findBoardById(UUID boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardByIdNotFoundException(boardId));

        return BoardMapper.toBoardResponseDto(board);
    }

    /**
     * Создаёт новую сущность на основе DTO объекта
     * и присваивает новые права OWNER для владельца запроса.
     *
     * @param boardDto DTO объект содержащий информацию о новом Эпике.
     * @return DTO объект BoardResponseDto новой сущности Board.
     */
    @Override
    public BoardResponseDto createBoard(UUID userId, BoardDto boardDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserByIdNotFoundException(userId));

        Board board = new Board(boardDto);
        board.addUser(user);
        board = boardRepository.save(board);

        Entitlement newEntitlement = new Entitlement(board, user, EntitlementEnum.OWNER);
        newEntitlement = entitlementRepository.save(newEntitlement);

        user.addEntitlement(newEntitlement);
        userRepository.save(user);

        return BoardMapper.toBoardResponseDto(board);
    }

    /**
     * Отправляет приглашение пользователю на присоединение к доске.
     * Отправить приглашения могут только пользователи у которых права доступа OWNER.
     *
     * @param ownerId Id владельца запроса в формате UUID.
     * @param boardId Id доски в формате UUID.
     * @param userId  Id приглашаемого пользователя в формате UUID.
     */
    @Override
    public void inviteUser(UUID ownerId, UUID boardId, UUID userId) {
        Entitlement entitlement = entitlementRepository.findByUserIdAndBoardId(ownerId, boardId)
                .orElseThrow(() -> new UserRightsByUserIdAndBoardIdNotFoundException(ownerId, boardId));

        if (!entitlement.getEntitlement().equals(EntitlementEnum.OWNER)) {
            throw new UserAccessDeniedException(ownerId);
        }

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardByIdNotFoundException(boardId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserByIdNotFoundException(userId));

        Invite invite = new Invite(user, board);
        inviteRepository.save(invite);
    }

    /**
     * Выдаёт права пользователя другим пользователям в доске.
     * Выдать права могут только администраторы и владельцы доски.
     * Администраторы доски не могут выдать права OWNER другим пользователям.
     * Никто не может присвоить новые права пользователю с правами OWNER
     *
     * @param ownerId     Id владельца запроса в формате UUID.
     * @param boardId     Id доски в формате UUID.
     * @param userId      Id пользователя, которому собираются присвоить права.
     * @param entitlement Наименование новых прав пользователя.
     * @throws UserRightsByUserIdAndBoardIdNotFoundException если права пользователя в доске не найдены.
     * @throws UserAccessDeniedException                     если владельцу запроса отказано в доступе к изменению прав.
     * @throws UserByIdNotFoundException                     если пользователь, которому собираются изменить права не найден.
     */
    @Override
    public void issueEntitlement(UUID ownerId, UUID boardId, UUID userId, EntitlementEnum entitlement) {
        EntitlementEnum ownerEntitlement = entitlementRepository.findByUserIdAndBoardId(ownerId, boardId)
                .orElseThrow(() -> new UserRightsByUserIdAndBoardIdNotFoundException(ownerId, boardId))
                .getEntitlement();

        if (ownerEntitlement.equals(EntitlementEnum.USER) || ownerEntitlement.equals(EntitlementEnum.EDITOR)) {
            throw new UserAccessDeniedException(ownerId);
        }

        if (ownerEntitlement.equals(EntitlementEnum.ADMIN) && entitlement.equals(EntitlementEnum.OWNER)) {
            throw new UserAccessDeniedException(ownerId);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserByIdNotFoundException(userId));

        Entitlement userEntitlement = entitlementRepository.findByUserIdAndBoardId(userId, boardId)
                .orElseThrow(() -> new UserRightsByUserIdAndBoardIdNotFoundException(userId, boardId));

        if (userEntitlement.getEntitlement().equals(EntitlementEnum.OWNER)) {
            throw new UserAccessDeniedException(ownerId);
        }

        user.getEntitlements().remove(userEntitlement);

        userEntitlement.setEntitlement(entitlement);
        userEntitlement = entitlementRepository.save(userEntitlement);

        user.addEntitlement(userEntitlement);
        userRepository.save(user);
    }

    /**
     * Обновляет доску на основе DTO объекта.
     * Обновить доску могут только пользователи с правами OWNER в этой доске.
     *
     * @param boardId  Id сущности в формате UUID.
     * @param boardDto DTO объект содержащий информацию об обновлённой Доске.
     * @return DTO объект новой доски.
     * @throws BoardByIdNotFoundException                    В случае если доска не найдена.
     * @throws UserRightsByUserIdAndBoardIdNotFoundException если права пользователя в доске не найдены.
     * @throws UserAccessDeniedException                     если владельцу запроса отказано в доступе к изменению прав.
     */
    @Override
    public BoardResponseDto updateBoardById(UUID userId, UUID boardId, BoardDto boardDto) {
        Entitlement entitlement = entitlementRepository.findByUserIdAndBoardId(userId, boardId)
                .orElseThrow(() -> new UserRightsByUserIdAndBoardIdNotFoundException(userId, boardId));

        if (!entitlement.getEntitlement().equals(EntitlementEnum.OWNER)) {
            throw new UserAccessDeniedException(userId);
        }

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardByIdNotFoundException(boardId));

        if (boardDto.getName() != null) {
            board.setName(boardDto.getName());

            board = boardRepository.save(board);
        }

        return BoardMapper.toBoardResponseDto(board);
    }

    /**
     * Удаляет доску по Id. Также удаляет все связанные сущности.
     * Удалить доску могут только пользователи с права OWNER.
     *
     * @param userId  Id владельца запроса в формате UUID.
     * @param boardId Id доски в формате UUID.
     * @throws BoardByIdNotFoundException                    В случае если доска не найдена.
     * @throws UserRightsByUserIdAndBoardIdNotFoundException если права пользователя в доске не найдены.
     * @throws UserAccessDeniedException                     если владельцу запроса отказано в доступе к изменению прав.
     */
    @Override
    public void deleteBoardById(UUID userId, UUID boardId) {
        Entitlement entitlement = entitlementRepository.findByUserIdAndBoardId(userId, boardId)
                .orElseThrow(() -> new UserRightsByUserIdAndBoardIdNotFoundException(userId, boardId));

        if (!entitlement.getEntitlement().equals(EntitlementEnum.OWNER)) {
            throw new UserAccessDeniedException(userId);
        }

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardByIdNotFoundException(boardId));

        Set<Epic> epics = board.getEpics();

        for (Epic epic : epics) {
            Set<Task> tasks = epic.getTasks();
            taskRepository.deleteAll(tasks);
        }

        List<Invite> invitations = inviteRepository.findAllByBoardId(boardId);

        inviteRepository.deleteAll(invitations);
        entitlementRepository.deleteAllByBoardId(boardId);
        epicRepository.deleteAll(epics);
        boardRepository.deleteById(boardId);
    }
}
