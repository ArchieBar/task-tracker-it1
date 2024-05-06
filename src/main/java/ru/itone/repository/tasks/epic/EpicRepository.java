package ru.itone.repository.tasks.epic;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.itone.model.tasks.epic.Epic;

import java.util.UUID;

@Repository
public interface EpicRepository extends JpaRepository<Epic, UUID> {
}
