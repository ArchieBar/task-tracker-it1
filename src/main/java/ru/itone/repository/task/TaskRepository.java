package ru.itone.repository.task;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itone.model.tasks.task.Task;

public interface TaskRepository extends JpaRepository<Task, String> {
}
