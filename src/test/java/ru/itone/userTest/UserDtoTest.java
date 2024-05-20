package ru.itone.userTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.itone.model.user.dto.UserFullNameAndEmailDto;
import ru.itone.model.user.dto.UserResponseDto;

import java.io.IOException;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserDtoTest {
    private final JacksonTester<UserResponseDto> jsonResponseDto;
    private final JacksonTester<UserFullNameAndEmailDto> jsonFullNameAndEmail;

    @Test
    public void testUserResponseDto() throws IOException {
        UUID userId = UUID.randomUUID();
        UserResponseDto dto = new UserResponseDto(
                userId,
                "Имя Фамилия",
                "example@email.ru"
        );

        JsonContent<UserResponseDto> result = jsonResponseDto.write(dto);

        assertThat(result).extractingJsonPathStringValue("$.id").isEqualTo(userId.toString());
        assertThat(result).extractingJsonPathStringValue("$.fullName").isEqualTo("Имя Фамилия");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("example@email.ru");
    }

    @Test
    public void testUserFullNameAndEmailDto() throws IOException {
        UserFullNameAndEmailDto dto = new UserFullNameAndEmailDto(
                "Имя Фамилия",
                "email@example.com"
        );

        JsonContent<UserFullNameAndEmailDto> result = jsonFullNameAndEmail.write(dto);

        assertThat(result).extractingJsonPathStringValue("$.fullName").isEqualTo("Имя Фамилия");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("email@example.com");
    }
}
