package ru.itone.userTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.itone.controller.UserController;
import ru.itone.filter.HttpLogonCheck;
import ru.itone.model.board.dto.BoardResponseDto;
import ru.itone.model.user.dto.LoginFormDto;
import ru.itone.model.user.dto.RegisterFormDto;
import ru.itone.model.user.dto.UserDto;
import ru.itone.model.user.dto.UserResponseDto;
import ru.itone.service.user.UserService;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {
    @MockBean
    private UserService service;

    @MockBean
    private HttpLogonCheck httpLogonCheck;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    private UserResponseDto userDto;
    private BoardResponseDto boardDto;

    @BeforeEach
    public void setUp() {
        userDto = new UserResponseDto(
                UUID.randomUUID(),
                "Полное имя",
                "email@example.com"
        );

        boardDto = new BoardResponseDto(
                UUID.randomUUID(),
                "Имя",
                new ArrayList<>(),
                new ArrayList<>()
        );
    }

    @Test
    public void checkThatTheRequestFindUserByIdIsSuccessfulWithTheCorrectParameters() throws Exception {
        when(httpLogonCheck.preHandle(any(), any(), any()))
                .thenReturn(true);
        when(service.findUserById(any()))
                .thenReturn(userDto);

        mvc.perform(get("/user/" + userDto.getId())
                        .header("X-User-Id", UUID.randomUUID()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId().toString()), String.class));
    }

    @Test
    public void checkThatTheRequestFindInviteByUserIsSuccessfulWithTheCorrectParameters() throws Exception {
        when(httpLogonCheck.preHandle(any(), any(), any()))
                .thenReturn(true);
        when(service.findInviteByUser(any()))
                .thenReturn(List.of(boardDto));

        mvc.perform(get("/user/get-invitations")
                        .header("X-User-Id", UUID.randomUUID()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(boardDto.getId().toString()), String.class));
    }

    @Test
    public void checkThatTheRequestRegisterUserIsSuccessfulWithTheCorrectParameters() throws Exception {
        when(httpLogonCheck.preHandle(any(), any(), any()))
                .thenReturn(true);
        when(service.registerUser(any()))
                .thenReturn(userDto);

        RegisterFormDto registerFormDto = new RegisterFormDto(
                "Имя",
                "Фамилия",
                "email@example.com",
                "password"
        );

        mvc.perform(post("/user/register")
                        .header("X-User-Id", UUID.randomUUID())
                        .content(mapper.writeValueAsString(registerFormDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(userDto.getId().toString()), String.class));
    }

    @Test
    public void checkThatTheRequestLoginIsSuccessfulWithTheCorrectParameters() throws Exception {
        when(httpLogonCheck.preHandle(any(), any(), any()))
                .thenReturn(true);
        when(service.login(any()))
                .thenReturn(userDto);

        LoginFormDto loginFormDto = new LoginFormDto(
                "email@example.com",
                "password"
        );

        mvc.perform(patch("/user/login")
                        .header("X-User-Id", UUID.randomUUID())
                        .content(mapper.writeValueAsString(loginFormDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId().toString()), String.class));
    }

    @Test
    public void checkThatTheRequestLogoutIsSuccessfulWithTheCorrectParameters() throws Exception {
        when(httpLogonCheck.preHandle(any(), any(), any()))
                .thenReturn(true);
        when(service.logout(any()))
                .thenReturn(userDto);

        mvc.perform(patch("/user/logout")
                        .header("X-User-Id", UUID.randomUUID()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId().toString()), String.class));
    }

    @Test
    public void checkThatTheRequestConfirmInviteIsSuccessfulWithTheCorrectParameters() throws Exception {
        when(httpLogonCheck.preHandle(any(), any(), any()))
                .thenReturn(true);
        doNothing().when(service).confirmInvite(any(), any());

        mvc.perform(patch("/user/" + boardDto.getId())
                        .header("X-User-Id", UUID.randomUUID()))
                .andExpect(status().isOk())
                .andExpect(content().string("Приглашение принято успешно."));
    }

    @Test
    public void checkThatTheRequestUpdateUserIsSuccessfulWithTheCorrectParameters() throws Exception {
        when(httpLogonCheck.preHandle(any(), any(), any()))
                .thenReturn(true);
        when(service.updateUserById(any(), any()))
                .thenReturn(userDto);

        UserDto newUserDto = new UserDto(
                "Имя",
                "Фамилия",
                "email@example.com"
        );

        mvc.perform(patch("/user")
                        .header("X-User-Id", UUID.randomUUID())
                        .content(mapper.writeValueAsString(newUserDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId().toString()), String.class));
    }

    @Test
    public void checkThatTheRequestDeleteUserByIdIsSuccessfulWithTheCorrectParameters() throws Exception {
        when(httpLogonCheck.preHandle(any(), any(), any()))
                .thenReturn(true);
        doNothing().when(service).deleteUserById(any());

        mvc.perform(delete("/user")
                        .header("X-User-Id", UUID.randomUUID()))
                .andExpect(status().isNoContent());
    }
}

