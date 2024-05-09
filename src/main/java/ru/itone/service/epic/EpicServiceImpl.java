package ru.itone.service.epic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.itone.exception.epic.EpicByIdNotFoundException;
import ru.itone.model.epic.Epic;
import ru.itone.model.epic.EpicMapper;
import ru.itone.model.epic.dto.EpicDto;
import ru.itone.model.epic.dto.EpicResponseDto;
import ru.itone.model.task.Task;
import ru.itone.repository.epic.EpicRepository;
import ru.itone.repository.task.TaskRepository;

import java.util.List;
import java.util.UUID;

@Service
public class EpicServiceImpl implements EpicService {
    private final EpicRepository epicRepository;
    private final TaskRepository taskRepository;

    @Autowired
    public EpicServiceImpl(EpicRepository epicRepository, TaskRepository taskRepository) {
        this.epicRepository = epicRepository;
        this.taskRepository = taskRepository;
    }

    /**
     * Возвращает все Эпики постранично используя Pageable.
     *
     * @param pageable Формируется в контроллере исходя из параметров запроса.
     * @return Список DTO объектов EpicResponseDto сущностей Epic.
     */
    @Override
    public List<EpicResponseDto> findEpics(Pageable pageable) {
        List<Epic> epics = epicRepository.findAll(pageable).toList();

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
     * @return DTO объект EpicResponseDto новой сущности Epic.
     */
    @Override
    public EpicResponseDto createEpic(EpicDto epicDto) {
        Epic epic = new Epic(epicDto);

        return EpicMapper.toEpicResponseDto(epicRepository.save(epic));
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
     * @throws EpicByIdNotFoundException В случае если сущность не найдена.
     *                                   Сообщение: "Эпик с ID: '%s' не найден.". Обработка в ErrorHandler.
     */
    @Override
    public void deleteEpicById(UUID epicId) {
        Epic epic = epicRepository.findById(epicId)
                .orElseThrow(() -> new EpicByIdNotFoundException(epicId));

        List<Task> tasks = epic.getTasks();

        taskRepository.deleteAll(tasks);
        epicRepository.deleteById(epicId);
    }
}
