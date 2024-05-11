package ru.itone.service.epic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.itone.exception.board.BoardNotFoundByIdException;
import ru.itone.exception.epic.EpicByIdNotFoundException;
import ru.itone.model.board.Board;
import ru.itone.model.epic.Epic;
import ru.itone.model.epic.EpicMapper;
import ru.itone.model.epic.dto.EpicDto;
import ru.itone.model.epic.dto.EpicResponseDto;
import ru.itone.model.task.Task;
import ru.itone.repository.board.BoardRepository;
import ru.itone.repository.epic.EpicRepository;
import ru.itone.repository.task.TaskRepository;

import java.util.List;
import java.util.UUID;

@Service
public class EpicServiceImpl implements EpicService {
    private final BoardRepository boardRepository;
    private final EpicRepository epicRepository;
    private final TaskRepository taskRepository;

    @Autowired
    public EpicServiceImpl(BoardRepository boardRepository,
                           EpicRepository epicRepository,
                           TaskRepository taskRepository) {
        this.boardRepository = boardRepository;
        this.epicRepository = epicRepository;
        this.taskRepository = taskRepository;
    }

    /**
     * Возвращает все Эпики одной доски по Id постранично используя Pageable.
     *
     * @param boardId Id доски к которой принадлежит эпик.
     * @return Список DTO объектов EpicResponseDto сущностей Epic.
     * @throws BoardNotFoundByIdException В случае если сущность не найдена.
     *                                    Сообщение: "Доска задач с ID: '%s' не найдена.". Обработка в ErrorHandler.
     */

    //TODO
    // Подумать как я могу использовать board
    @Override
    public List<EpicResponseDto> findEpicsByBoardId(UUID boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardNotFoundByIdException(boardId));

        List<Epic> epics = board.getEpics();

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
    public EpicResponseDto createEpic(UUID boardId, EpicDto epicDto) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardNotFoundByIdException(boardId));

        Epic epic = new Epic(epicDto);

        epic = epicRepository.save(epic);

        board.addEpic(epic);
        boardRepository.save(board);

        return EpicMapper.toEpicResponseDto(epic);
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
    public EpicResponseDto updateEpicById(UUID epicId, EpicDto epicDto) {
        Epic epic = epicRepository.findById(epicId)
                .orElseThrow(() -> new EpicByIdNotFoundException(epicId));

        if (epicDto.getName() != null) {
            epic.setName(epicDto.getName());
        }

        if (epicDto.getDescription() != null) {
            epic.setDescription(epicDto.getDescription());
        }

        return EpicMapper.toEpicResponseDto(epicRepository.save(epic));
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
    public void deleteEpicById(UUID boardId, UUID epicId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardNotFoundByIdException(boardId));

        Epic epic = board.getEpics().stream()
                .filter(e -> e.getId().equals(epicId))
                .findFirst()
                .orElseThrow(() -> new EpicByIdNotFoundException(epicId));

        List<Task> tasks = epic.getTasks();

        board.getEpics().remove(epic);
        boardRepository.save(board);

        taskRepository.deleteAll(tasks);
        epicRepository.deleteById(epicId);
    }
}
