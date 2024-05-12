package ru.itone.service.board;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.itone.exception.board.BoardNotFoundByIdException;
import ru.itone.model.board.Board;
import ru.itone.model.board.BoardMapper;
import ru.itone.model.board.dto.BoardDto;
import ru.itone.model.board.dto.BoardResponseDto;
import ru.itone.model.epic.Epic;
import ru.itone.model.task.Task;
import ru.itone.repository.BoardRepository;
import ru.itone.repository.EpicRepository;
import ru.itone.repository.TaskRepository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class BoardServiceImpl implements BoardService {
    private final BoardRepository boardRepository;
    private final EpicRepository epicRepository;
    private final TaskRepository taskRepository;

    @Autowired
    public BoardServiceImpl(BoardRepository boardRepository,
                            EpicRepository epicRepository,
                            TaskRepository taskRepository) {
        this.boardRepository = boardRepository;
        this.epicRepository = epicRepository;
        this.taskRepository = taskRepository;
    }

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
     * @throws BoardNotFoundByIdException В случае если сущность не найдена.
     *                                    Сообщение: "Доска задач с ID: '%s' не найден.". Обработка в ErrorHandler.
     */
    @Override
    public BoardResponseDto findBoardById(UUID boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardNotFoundByIdException(boardId));

        return BoardMapper.toBoardResponseDto(board);
    }

    /**
     * Создаёт новую сущность на основе DTO объекта. Id генерируется на уровне бд.
     *
     * @param boardDto DTO объект содержащий информацию о новом Эпике.
     * @return DTO объект BoardResponseDto новой сущности Board.
     */
    @Override
    public BoardResponseDto createBoard(BoardDto boardDto) {
        Board board = new Board(boardDto);

        return BoardMapper.toBoardResponseDto(boardRepository.save(board));
    }

    /**
     * Обновляет Board на основе DTO объекта.
     *
     * @param boardId  Id сущности в формате UUID.
     * @param boardDto DTO объект содержащий информацию об обновлённой Доске.
     * @return DTO объект BoardResponseDto новой сущности Board.
     * @throws BoardNotFoundByIdException В случае если сущность не найдена.
     *                                    Сообщение: "Доска задач с ID: '%s' не найден.". Обработка в ErrorHandler.
     */
    @Override
    public BoardResponseDto updateBoardById(UUID boardId, BoardDto boardDto) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardNotFoundByIdException(boardId));

        if (boardDto.getName() != null) {
            board.setName(boardDto.getName());

            board = boardRepository.save(board);
        }

        return BoardMapper.toBoardResponseDto(board);
    }

    /**
     * Удаляет сущность по Id. Также удаляет все связанные сущности эпиков и задач.
     *
     * @param boardId Id в формате UUID.
     * @throws BoardNotFoundByIdException В случае если сущность не найдена.
     *                                    Сообщение: "Доска задач с ID: '%s' не найден.". Обработка в ErrorHandler.
     */
    @Override
    public void deleteBoardById(UUID boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardNotFoundByIdException(boardId));

        Set<Epic> epics = board.getEpics();

        for (Epic epic : epics) {
            Set<Task> tasks = epic.getTasks();
            taskRepository.deleteAll(tasks);
        }

        epicRepository.deleteAll(epics);
        boardRepository.deleteById(boardId);
    }
}
