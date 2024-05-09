package ru.itone.service.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.itone.exception.epic.EpicByIdNotFoundException;
import ru.itone.exception.task.TaskByIdNotFoundException;
import ru.itone.model.epic.Epic;
import ru.itone.model.epic.EpicStatus;
import ru.itone.model.task.Task;
import ru.itone.model.task.TaskMapper;
import ru.itone.model.task.dto.TaskDto;
import ru.itone.model.task.dto.TaskResponseDto;
import ru.itone.repository.epic.EpicRepository;
import ru.itone.repository.task.TaskRepository;

import java.util.List;
import java.util.UUID;

@Service
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final EpicRepository epicRepository;

    @Autowired
    public TaskServiceImpl(TaskRepository taskRepository, EpicRepository epicRepository) {
        this.taskRepository = taskRepository;
        this.epicRepository = epicRepository;
    }

    /**
     * Находит список задач по ID эпика.
     *
     * @param epicId ID эпика в формате UUID.
     * @return Список DTO объектов TaskResponseDto сущностей Task.
     * @throws EpicByIdNotFoundException В случае если сущность не найдена.
     *                                   Сообщение: "Эпик с ID: '%s' не найден.". Обработка в ErrorHandler.
     */
    @Override
    public List<TaskResponseDto> findTasksByEpicId(UUID epicId) {
        Epic epic = epicRepository.findById(epicId)
                .orElseThrow(() -> new EpicByIdNotFoundException(epicId));

        List<Task> tasks = epic.getTasks();

        return TaskMapper.toTaskResponseDtoList(tasks);
    }

    /**
     * Находит сущность по ID.
     *
     * @param epicId ID эпика в формате UUID.
     * @param taskId ID задачи в формате UUID.
     * @return DTO объект TaskResponseDto сущности Task.
     * @throws EpicByIdNotFoundException В случае если сущность не найдена.
     *                                   Сообщение: "Эпик с ID: '%s' не найден.". Обработка в ErrorHandler.
     */
    @Override
    public TaskResponseDto findTaskById(UUID epicId, UUID taskId) {
        Epic epic = epicRepository.findById(epicId)
                .orElseThrow(() -> new EpicByIdNotFoundException(epicId));

        List<Task> tasks = epic.getTasks();

        Task task = tasks.stream()
                .filter(t -> t.getId().equals(taskId))
                .findFirst()
                .orElseThrow(() -> new TaskByIdNotFoundException(taskId));

        return TaskMapper.toTaskResponseDto(task);
    }

    /**
     * Создаёт новую сущность на основе DTO объекта.
     *
     * @param epicId  ID эпика в формате UUID.
     * @param taskDto DTO объект содержащий информацию о новой сущности.
     * @return DTO объект TaskResponseDto новой сущности Task.
     * @throws EpicByIdNotFoundException В случае если сущность не найдена.
     *                                   Сообщение: "Эпик с ID: '%s' не найден.". Обработка в ErrorHandler.
     */
    @Override
    public TaskResponseDto createTaskById(UUID epicId, TaskDto taskDto) {
        Epic epic = epicRepository.findById(epicId)
                .orElseThrow(() -> new EpicByIdNotFoundException(epicId));

        Task task = new Task(taskDto);
        task = taskRepository.save(task);

        List<Task> tasks = epic.getTasks();
        tasks.add(task);

        epic.setTasks(tasks);
        EpicStatus status = checkStatus(tasks);
        epic.setStatus(status);
        epicRepository.save(epic);

        return TaskMapper.toTaskResponseDto(task);
    }

    /**
     * Обновляет существующую сущность на основе DTO объекта.
     *
     * @param epicId  ID эпика в формате UUID.
     * @param taskId  ID задачи в формате UUID.
     * @param taskDto DTO объект содержащий информацию об обновляемой сущности.
     * @return DTO объект TaskResponseDto обновлённой сущности Task.
     * @throws EpicByIdNotFoundException В случае если сущность не найдена.
     *                                   Сообщение: "Эпик с ID: '%s' не найден.". Обработка в ErrorHandler.
     * @throws TaskByIdNotFoundException В случае если сущность не найдена.
     *                                   Сообщение: "Задача с ID: '%s' не найдена.". Обработка в ErrorHandler.
     */
    //TODO
    // Не уверен, что нужно сохранять Эпик с новым значением таски
    @Override
    public TaskResponseDto updateTaskById(UUID epicId, UUID taskId, TaskDto taskDto) {
        Epic epic = epicRepository.findById(epicId)
                .orElseThrow(() -> new EpicByIdNotFoundException(epicId));

        List<Task> tasks = epic.getTasks();

        Task task = tasks.stream()
                .filter(t -> t.getId().equals(taskId))
                .findFirst()
                .orElseThrow(() -> new TaskByIdNotFoundException(taskId));

        if (taskDto.getDescription() != null) {
            task.setDescription(taskDto.getDescription());
        }

        task = taskRepository.save(task);

        return TaskMapper.toTaskResponseDto(task);
    }

    /**
     * Обновляет статус задачи. Также обновляет статус эпика на основе списка задач.
     *
     * @param epicId    ID эпика в формате UUID.
     * @param taskId    ID задачи в формате UUID.
     * @param completed Boolean значение выполнения задачи.
     * @return DTO объект TaskResponseDto обновлённой сущности Task.
     * @throws EpicByIdNotFoundException В случае если сущность не найдена.
     *                                   Сообщение: "Эпик с ID: '%s' не найден.". Обработка в ErrorHandler.
     * @throws TaskByIdNotFoundException В случае если сущность не найдена.
     *                                   Сообщение: "Задача с ID: '%s' не найдена.". Обработка в ErrorHandler.
     */
    @Override
    public TaskResponseDto updateCompletedTask(UUID epicId, UUID taskId, Boolean completed) {
        Epic epic = epicRepository.findById(epicId)
                .orElseThrow(() -> new EpicByIdNotFoundException(epicId));

        List<Task> tasks = epic.getTasks();

        Task task = tasks.stream()
                .filter(t -> t.getId().equals(taskId))
                .findFirst()
                .orElseThrow(() -> new TaskByIdNotFoundException(taskId));

        tasks.remove(task);

        task.setIsCompleted(completed);
        tasks.add(task);
        taskRepository.save(task);

        epic.setTasks(tasks);
        EpicStatus status = checkStatus(tasks);
        epic.setStatus(status);
        epicRepository.save(epic);

        return TaskMapper.toTaskResponseDto(task);
    }

    /**
     * Удаляет задачу по её ID. Также обновляет статус эпика на основе оставшегося списка задач.
     *
     * @param epicId ID эпика в формате UUID.
     * @param taskId ID задачи в формате UUID.
     * @throws EpicByIdNotFoundException В случае если сущность не найдена.
     *                                   Сообщение: "Эпик с ID: '%s' не найден.". Обработка в ErrorHandler.
     * @throws TaskByIdNotFoundException В случае если сущность не найдена.
     *                                   Сообщение: "Задача с ID: '%s' не найдена.". Обработка в ErrorHandler.
     */
    @Override
    public void deleteTaskById(UUID epicId, UUID taskId) {
        Epic epic = epicRepository.findById(epicId)
                .orElseThrow(() -> new EpicByIdNotFoundException(epicId));

        List<Task> tasks = epic.getTasks();

        Task task = tasks.stream()
                .filter(t -> t.getId().equals(taskId))
                .findFirst()
                .orElseThrow(() -> new TaskByIdNotFoundException(taskId));

        tasks.remove(task);
        taskRepository.deleteById(taskId);

        epic.setTasks(tasks);
        EpicStatus status = checkStatus(tasks);
        epic.setStatus(status);
        epicRepository.save(epic);
    }

    /**
     * Алгоритм для проверки статуса Эпика, он работает следующим образом:
     * Инициализирует две boolean переменные hasTrue и hasFalse со значениями false.
     * Проходимся по всему списку задач и если поле isCompleted = true присваиваем true переменной hasTrue,
     * иначе присваиваем true переменной hasFalse.
     * Если во время прохода по списку hasTrue и hasFalse равны true - возвращаем статус EpicStatus.DOING.
     * Если после прохода списка значение hasTrue = true, а значение hasFalse = false - возвращаем статус EpicStatus.DONE.
     * Если после прохода списка значение hasTrue = false, а значение hasFalse = true - возвращаем статус EpicStatus.TODO.
     *
     * @param tasks Список задач Эпика.
     * @return Enum статус задачи эпика. Он может быть равен: .TODO, .DONE, .DOING.
     */
    //TODO
    // Добавить проверку на NPE
    private EpicStatus checkStatus(List<Task> tasks) {
        boolean hasTrue = false;
        boolean hasFalse = false;

        for (Task task : tasks) {
            if (task.getIsCompleted()) {
                hasTrue = true;
            } else {
                hasFalse = true;
            }

            if (hasTrue && hasFalse) {
                return EpicStatus.DOING;
            }
        }

        if (hasTrue) {
            return EpicStatus.DONE;
        } else {
            return EpicStatus.TODO;
        }
    }
}
