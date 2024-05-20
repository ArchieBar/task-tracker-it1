package ru.itone.epicTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.itone.controller.EpicController;
import ru.itone.filter.HttpLogonCheck;
import ru.itone.model.epic.comment.dto.CommentDto;
import ru.itone.model.epic.comment.dto.CommentResponseDto;
import ru.itone.model.epic.dto.EpicDto;
import ru.itone.model.epic.dto.EpicResponseDto;
import ru.itone.model.user.dto.UserFullNameAndEmailDto;
import ru.itone.service.epic.EpicService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(controllers = EpicController.class)
public class EpicControllerTest {
    @MockBean
    private EpicService service;

    @MockBean
    private HttpLogonCheck httpLogonCheck;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    private EpicResponseDto dto;
    private CommentResponseDto commentResponseDto;

    @BeforeEach
    public void setUp() {
        dto = new EpicResponseDto(
                UUID.randomUUID(),
                "Имя",
                "Описание",
                "TODO",
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()
        );

        commentResponseDto = new CommentResponseDto(
                UUID.randomUUID(),
                "Текст",
                LocalDateTime.now().minusDays(1),
                new UserFullNameAndEmailDto(
                        "Имя Фамилия",
                        "email@email.email"
                )
        );
    }

    @Test
    public void checkThatTheRequestFindEpicsByBoardIdIsSuccessfulWithTheCorrectParameters() throws Exception {
        when(httpLogonCheck.preHandle(any(), any(), any()))
                .thenReturn(true);
        when(service.findEpicsByBoardId(any()))
                .thenReturn(List.of(dto));

        mvc.perform(get("/epic/all/" + UUID.randomUUID())
                        .header("X-User-Id", UUID.randomUUID()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(dto.getId().toString()), String.class));
    }

    @Test
    public void checkThatTheRequestFindEpicByIdIsSuccessfulWithTheCorrectParameters() throws Exception {
        when(httpLogonCheck.preHandle(any(), any(), any()))
                .thenReturn(true);
        when(service.findEpicById(any()))
                .thenReturn(dto);

        mvc.perform(get("/epic/" + dto.getId())
                        .header("X-User-Id", UUID.randomUUID()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(dto.getId().toString()), String.class));
    }

    @Test
    public void checkThatTheRequestCreateEpicIsSuccessfulWithTheCorrectParameters() throws Exception {
        when(httpLogonCheck.preHandle(any(), any(), any()))
                .thenReturn(true);
        when(service.createEpic(any(), any(), any()))
                .thenReturn(dto);

        EpicDto epicDto = new EpicDto("Имя", "Описание", LocalDateTime.now().plusDays(1));

        mvc.perform(post("/epic/" + UUID.randomUUID())
                        .header("X-User-Id", UUID.randomUUID())
                        .content(mapper.writeValueAsString(epicDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(dto.getId().toString()), String.class));
    }

    @Test
    public void checkThatTheRequestCreatedCommentByEpicIdIsSuccessfulWithTheCorrectParameters() throws Exception {
        when(httpLogonCheck.preHandle(any(), any(), any()))
                .thenReturn(true);
        when(service.createCommentByEpicId(any(), any(), any()))
                .thenReturn(commentResponseDto);

        CommentDto commentDto = new CommentDto("Комментарий");

        mvc.perform(post("/epic/" + UUID.randomUUID() + "/comment")
                        .header("X-User-Id", UUID.randomUUID())
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(commentResponseDto.getId().toString()), String.class));
    }

    @Test
    public void checkThatTheRequestUpdateEpicByIdIsSuccessfulWithTheCorrectParameters() throws Exception {
        when(httpLogonCheck.preHandle(any(), any(), any()))
                .thenReturn(true);
        when(service.updateEpicById(any(), any(), any()))
                .thenReturn(dto);

        EpicDto epicDto = new EpicDto("Имя", "Описание", LocalDateTime.now().plusDays(1));

        mvc.perform(patch("/epic/" + dto.getId())
                        .header("X-User-Id", UUID.randomUUID())
                        .content(mapper.writeValueAsString(epicDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(dto.getId().toString()), String.class));
    }

    @Test
    public void checkThatTheRequestTakeEpicIsSuccessfulWithTheCorrectParameters() throws Exception {
        when(httpLogonCheck.preHandle(any(), any(), any()))
                .thenReturn(true);
        doNothing().when(service).takeEpic(any(), any());

        mvc.perform(patch("/epic/take/" + dto.getId())
                        .header("X-User-Id", UUID.randomUUID()))
                .andExpect(status().isOk())
                .andExpect(content().string("Теперь вы выполняете этот эпик."));
    }

    @Test
    public void checkThatTheRequestRefuseEpicIsSuccessfulWithTheCorrectParameters() throws Exception {
        when(httpLogonCheck.preHandle(any(), any(), any()))
                .thenReturn(true);
        doNothing().when(service).refuseEpic(any(), any());

        mvc.perform(patch("/epic/refuse/" + dto.getId())
                        .header("X-User-Id", UUID.randomUUID()))
                .andExpect(status().isOk())
                .andExpect(content().string("Вы отказались от выполнения этого эпика."));
    }

    @Test
    public void checkThatTheRequestUpdateCommentByIdIsSuccessfulWithTheCorrectParameters() throws Exception {
        when(httpLogonCheck.preHandle(any(), any(), any()))
                .thenReturn(true);
        when(service.updateCommentById(any(), any(), any()))
                .thenReturn(commentResponseDto);

        CommentDto commentDto = new CommentDto("Комментарий");

        mvc.perform(patch("/epic/comment/" + commentResponseDto.getId())
                        .header("X-User-Id", UUID.randomUUID())
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentResponseDto.getId().toString()), String.class));
    }

    @Test
    public void checkThatTheRequestDeleteEpicByIdIsSuccessfulWithTheCorrectParameters() throws Exception {
        when(httpLogonCheck.preHandle(any(), any(), any()))
                .thenReturn(true);
        doNothing().when(service).deleteEpicById(any(), any(), any());

        mvc.perform(delete("/epic/" + UUID.randomUUID() + "/" + dto.getId())
                        .header("X-User-Id", UUID.randomUUID()))
                .andExpect(status().isNoContent());
    }

    @Test
    public void checkThatTheRequestDeleteCommentByIdIsSuccessfulWithTheCorrectParameters() throws Exception {
        when(httpLogonCheck.preHandle(any(), any(), any()))
                .thenReturn(true);
        doNothing().when(service).deleteCommentById(any(), any(), any());

        mvc.perform(delete("/epic/" + dto.getId() + "/comment/" + commentResponseDto.getId())
                        .header("X-User-Id", UUID.randomUUID()))
                .andExpect(status().isNoContent());
    }
}



