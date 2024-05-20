package ru.itone.epicTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.itone.model.epic.EpicStatus;
import ru.itone.model.epic.comment.dto.CommentResponseDto;
import ru.itone.model.epic.dto.EpicResponseDto;
import ru.itone.model.task.dto.TaskResponseDto;
import ru.itone.model.user.dto.UserFullNameAndEmailDto;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class EpicDtoTest {
    private final JacksonTester<EpicResponseDto> jsonResponseDto;

    @Test
    public void testEpicResponseDto() throws IOException {
        UUID taskId = UUID.randomUUID();
        TaskResponseDto taskResponseDto = new TaskResponseDto(
                taskId,
                "Описание задачи",
                false
        );

        UserFullNameAndEmailDto userFullNameAndEmailDto = new UserFullNameAndEmailDto(
                "Имя Фамилия",
                "email@example.com"
        );

        UUID commentId = UUID.randomUUID();
        LocalDateTime createdCommentTime = LocalDateTime.now().minusHours(23);
        CommentResponseDto commentResponseDto = new CommentResponseDto(
                commentId,
                "Текст",
                createdCommentTime,
                userFullNameAndEmailDto
        );

        UUID epicId = UUID.randomUUID();
        LocalDateTime createdEpicTime = LocalDateTime.now().minusDays(1);
        LocalDateTime endEpicTime = LocalDateTime.now().plusDays(1);
        EpicResponseDto dto = new EpicResponseDto(
                epicId,
                "Название",
                "Описание эпика",
                EpicStatus.TODO.toString(),
                createdEpicTime,
                endEpicTime,
                List.of(taskResponseDto),
                List.of(commentResponseDto),
                List.of(userFullNameAndEmailDto)
        );

        JsonContent<EpicResponseDto> result = jsonResponseDto.write(dto);

        // EpicResponseDto
        assertThat(result).extractingJsonPathStringValue("$.id").isEqualTo(epicId.toString());
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Название");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Описание эпика");
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo(EpicStatus.TODO.toString());

        // TaskResponseDto
        assertThat(result).extractingJsonPathStringValue("$.tasks[0].id").isEqualTo(taskId.toString());
        assertThat(result).extractingJsonPathStringValue("$.tasks[0].description").isEqualTo("Описание задачи");
        assertThat(result).extractingJsonPathBooleanValue("$.tasks[0].isCompleted").isEqualTo(false);

        // CommentResponseDto
        assertThat(result).extractingJsonPathStringValue("$.activity[0].id").isEqualTo(commentId.toString());
        assertThat(result).extractingJsonPathStringValue("$.activity[0].text").isEqualTo("Текст");
        assertThat(result).extractingJsonPathStringValue("$.activity[0].createdTime").isNotNull();

        // UserFullNameAndEmailDto
        assertThat(result).extractingJsonPathStringValue("$.activity[0].author.fullName").isEqualTo("Имя Фамилия");
        assertThat(result).extractingJsonPathStringValue("$.activity[0].author.email").isEqualTo("email@example.com");
    }
}
