package ru.itone.service.tasks.epic;

import org.springframework.data.domain.Pageable;
import ru.itone.model.tasks.epic.dto.EpicDto;
import ru.itone.model.tasks.epic.dto.EpicResponseDto;

import java.util.List;
import java.util.UUID;

public interface EpicService {
    List<EpicResponseDto> findEpics(Pageable pageable);

    EpicResponseDto findEpicById(UUID epicId);

    EpicResponseDto createEpic(EpicDto epicDto);

    EpicResponseDto updateEpicById(UUID epicId, EpicDto epicDto);

    void deleteEpicById(UUID epicId);
}
