package ru.itone.boardTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.itone.controller.BoardController;
import ru.itone.filter.HttpLogonCheck;
import ru.itone.model.board.dto.BoardDto;
import ru.itone.model.board.dto.BoardResponseDto;
import ru.itone.service.board.BoardService;

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

@WebMvcTest(controllers = BoardController.class)
public class BoardControllerTest {
    @MockBean
    private BoardService service;

    @MockBean
    private HttpLogonCheck httpLogonCheck;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    private BoardResponseDto dto;

    @BeforeEach
    public void setUp() {
        dto = new BoardResponseDto(
                UUID.randomUUID(),
                "Имя",
                new ArrayList<>(),
                new ArrayList<>()
        );
    }

    @Test
    public void checkThatTheRequestFindBoardsIsSuccessfulWithTheCorrectParameters() throws Exception {
        when(httpLogonCheck.preHandle(any(), any(), any()))
                .thenReturn(true);
        when(service.findBoards(PageRequest.of(0, 10)))
                .thenReturn(List.of(dto));

        mvc.perform(get("/board")
                        .header("X-User-Id", UUID.randomUUID())
                        .param("page", String.valueOf(0))
                        .param("size", String.valueOf(10)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(dto.getId().toString()), String.class));
    }

    @Test
    public void checkThatTheRequestFindBoardByIdIsSuccessfulWithTheCorrectParameters() throws Exception {
        when(httpLogonCheck.preHandle(any(), any(), any()))
                .thenReturn(true);
        when(service.findBoardById(any()))
                .thenReturn(dto);

        mvc.perform(get("/board/" + dto.getId())
                        .header("X-User-Id", UUID.randomUUID()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(dto.getId().toString()), String.class));
    }

    @Test
    public void checkThatTheRequestCreateBoardIsSuccessfulWithTheCorrectParameters() throws Exception {
        when(httpLogonCheck.preHandle(any(), any(), any()))
                .thenReturn(true);
        when(service.createBoard(any(), any()))
                .thenReturn(dto);

        BoardDto boardDto = new BoardDto("Имя");

        mvc.perform(post("/board")
                        .header("X-User-Id", UUID.randomUUID())
                        .content(mapper.writeValueAsString(boardDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(dto.getId().toString()), String.class));
    }

    @Test
    public void checkThatTheRequestInviteUserIsSuccessfulWithTheCorrectParameters() throws Exception {
        when(httpLogonCheck.preHandle(any(), any(), any()))
                .thenReturn(true);
        doNothing().when(service).inviteUser(any(), any(), any());

        UUID boardId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        mvc.perform(patch("/board/" + boardId + "/invite/" + userId)
                        .header("X-User-Id", userId))
                .andExpect(content().string("Приглашение успешно отправлено."));
    }

    @Test
    public void checkThatTheRequestIssueEntitlementIsSuccessfulWithTheCorrectParameters() throws Exception {
        when(httpLogonCheck.preHandle(any(), any(), any()))
                .thenReturn(true);
        doNothing().when(service).issueEntitlement(any(), any(), any(), any());

        UUID boardId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        mvc.perform(patch("/board/" + boardId + "/issueLicense/" + userId)
                        .header("X-User-Id", userId)
                        .param("entitlement", "ADMIN"))
                .andExpect(content().string("Права пользователя обновлены."));
    }

    @Test
    public void checkThatTheRequestUpdateBoardByIdIsSuccessfulWithTheCorrectParameters() throws Exception {
        when(httpLogonCheck.preHandle(any(), any(), any()))
                .thenReturn(true);
        when(service.updateBoardById(any(), any(), any()))
                .thenReturn(dto);

        BoardDto boardDto = new BoardDto("Имя");

        mvc.perform(patch("/board/" + UUID.randomUUID())
                        .header("X-User-Id", UUID.randomUUID())
                        .content(mapper.writeValueAsString(boardDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(dto.getId().toString()), String.class));
    }

    @Test
    public void checkThatTheRequestDeleteBoardByIdIsSuccessfulWithTheCorrectParameters() throws Exception {
        when(httpLogonCheck.preHandle(any(), any(), any()))
                .thenReturn(true);
        doNothing().when(service).deleteBoardById(any(), any());

        mvc.perform(delete("/board/" + UUID.randomUUID())
                        .header("X-User-Id", UUID.randomUUID()))
                .andExpect(status().isNoContent());
    }
}
