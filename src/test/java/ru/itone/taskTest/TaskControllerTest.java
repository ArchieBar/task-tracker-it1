package ru.itone.taskTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.itone.controller.TaskController;
import ru.itone.filter.HttpLogonCheck;
import ru.itone.model.task.dto.TaskDto;
import ru.itone.model.task.dto.TaskResponseDto;
import ru.itone.service.task.TaskService;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TaskController.class)
public class TaskControllerTest {
    @MockBean
    private TaskService service;

    @MockBean
    private HttpLogonCheck httpLogonCheck;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    private TaskResponseDto dto;

    @BeforeEach
    public void setUp() {
        dto = new TaskResponseDto(
                UUID.randomUUID(),
                "Описание",
                false
        );
    }

    @Test
    public void checkThatTheRequestFindTasksByEpicIdIsSuccessfulWithTheCorrectParameters() throws Exception {
        when(httpLogonCheck.preHandle(any(), any(), any()))
                .thenReturn(true);
        when(service.findTasksByEpicId(any()))
                .thenReturn(List.of(dto));

        mvc.perform(get("/task/all/" + UUID.randomUUID())
                        .header("X-User-Id", UUID.randomUUID()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(dto.getId().toString()), String.class));
    }

    @Test
    public void checkThatTheRequestFindTaskByIdIsSuccessfulWithTheCorrectParameters() throws Exception {
        when(httpLogonCheck.preHandle(any(), any(), any()))
                .thenReturn(true);
        when(service.findTaskById(any()))
                .thenReturn(dto);

        mvc.perform(get("/task/" + dto.getId())
                        .header("X-User-Id", UUID.randomUUID()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(dto.getId().toString()), String.class));
    }

    @Test
    public void checkThatTheRequestCreateTaskInEpicIsSuccessfulWithTheCorrectParameters() throws Exception {
        when(httpLogonCheck.preHandle(any(), any(), any()))
                .thenReturn(true);
        when(service.createTaskById(any(), any(), any()))
                .thenReturn(dto);

        TaskDto taskDto = new TaskDto("Описание");

        mvc.perform(post("/task/" + UUID.randomUUID())
                        .header("X-User-Id", UUID.randomUUID())
                        .content(mapper.writeValueAsString(taskDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(dto.getId().toString()), String.class));
    }

    @Test
    public void checkThatTheRequestUpdateTaskByIdIsSuccessfulWithTheCorrectParameters() throws Exception {
        when(httpLogonCheck.preHandle(any(), any(), any()))
                .thenReturn(true);
        when(service.updateTaskById(any(), any(), any()))
                .thenReturn(dto);

        TaskDto taskDto = new TaskDto("Описание");

        mvc.perform(patch("/task/" + dto.getId())
                        .header("X-User-Id", UUID.randomUUID())
                        .content(mapper.writeValueAsString(taskDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(dto.getId().toString()), String.class));
    }

    @Test
    public void checkThatTheRequestUpdateCompletedTaskIsSuccessfulWithTheCorrectParameters() throws Exception {
        when(httpLogonCheck.preHandle(any(), any(), any()))
                .thenReturn(true);
        when(service.updateCompletedTask(any(), any(), any(), any()))
                .thenReturn(dto);

        mvc.perform(patch("/task/" + UUID.randomUUID() + "/" + dto.getId())
                        .header("X-User-Id", UUID.randomUUID())
                        .param("completed", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(dto.getId().toString()), String.class));
    }

    @Test
    public void checkThatTheRequestDeleteTaskByIdIsSuccessfulWithTheCorrectParameters() throws Exception {
        when(httpLogonCheck.preHandle(any(), any(), any()))
                .thenReturn(true);
        doNothing().when(service).deleteTaskById(any(), any(), any());

        mvc.perform(delete("/task/" + UUID.randomUUID() + "/" + dto.getId())
                        .header("X-User-Id", UUID.randomUUID()))
                .andExpect(status().isNoContent());
    }
}

