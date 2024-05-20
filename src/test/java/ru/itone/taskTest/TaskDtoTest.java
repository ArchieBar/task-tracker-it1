package ru.itone.taskTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.itone.model.task.dto.TaskResponseDto;

import java.io.IOException;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class TaskDtoTest {
    private final JacksonTester<TaskResponseDto> jsonResponseDto;

    @Test
    public void testTaskResponseDto() throws IOException {
        UUID taskId = UUID.randomUUID();
        TaskResponseDto dto = new TaskResponseDto(
                taskId,
                "Описание задачи",
                false
        );

        JsonContent<TaskResponseDto> result = jsonResponseDto.write(dto);

        assertThat(result).extractingJsonPathStringValue("$.id").isEqualTo(taskId.toString());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Описание задачи");
        assertThat(result).extractingJsonPathBooleanValue("$.isCompleted").isEqualTo(false);
    }
}
