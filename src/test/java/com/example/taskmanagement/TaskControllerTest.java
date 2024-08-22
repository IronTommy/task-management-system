package com.example.taskmanagement;

import com.example.taskmanagement.dto.AuthenticateDto;
import com.example.taskmanagement.dto.CommentDto;
import com.example.taskmanagement.dto.RegistrationDto;
import com.example.taskmanagement.dto.TaskDto;
import com.example.taskmanagement.entity.User;
import com.example.taskmanagement.service.AuthService;
import com.example.taskmanagement.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthService authService;
    @Autowired
    private UserService userService;

    private String accessToken;
    private UUID existingExecutorId;

    @BeforeEach
    public void setUp() throws Exception {
        String email = "executor" + System.currentTimeMillis() + "@example.com";

        RegistrationDto registrationDto = new RegistrationDto();
        registrationDto.setFirstName("Executor");
        registrationDto.setLastName("User");
        registrationDto.setEmail(email);
        registrationDto.setPassword1("password");
        registrationDto.setPassword2("password");

        User executor = userService.createNewUser(registrationDto);
        existingExecutorId = executor.getId();

        accessToken = authenticateAndGetToken(email, "password");
    }

    private String authenticateAndGetToken(String email, String password) throws Exception {
        AuthenticateDto authenticateDto = new AuthenticateDto();
        authenticateDto.setEmail(email);
        authenticateDto.setPassword(password);

        MockHttpServletResponse response = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(authenticateDto)))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        for (Cookie cookie : response.getCookies()) {
            if ("accessToken".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        throw new RuntimeException("Access token not found");
    }

    @Test
    public void testCreateTask() throws Exception {
        TaskDto taskDto = new TaskDto();
        taskDto.setTitle("Test Task");
        taskDto.setDescription("This is a test task");
        taskDto.setExecutorId(existingExecutorId);
        taskDto.setStatus("NEW");
        taskDto.setPriority("HIGH");

        mockMvc.perform(post("/api/tasks")
                        .cookie(new Cookie("accessToken", accessToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(taskDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Task"))
                .andExpect(jsonPath("$.executor.id").value(existingExecutorId.toString()));
    }

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testUpdateTask() throws Exception {
        // Создаем задачу
        TaskDto taskDto = new TaskDto();
        taskDto.setTitle("Original Task");
        taskDto.setDescription("Original description");
        taskDto.setStatus("NEW");
        taskDto.setPriority("LOW");
        taskDto.setExecutorId(existingExecutorId);

        MvcResult result = mockMvc.perform(post("/api/tasks")
                        .cookie(new Cookie("accessToken", accessToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(taskDto)))
                .andExpect(status().isOk())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        Long taskId = JsonPath.parse(responseContent).read("$.id", Long.class);


        // Обновляем задачу
        TaskDto updatedTaskDto = new TaskDto();
        updatedTaskDto.setExecutorId(existingExecutorId);

        updatedTaskDto.setTitle("Updated Task");
        updatedTaskDto.setDescription("Updated description");
        updatedTaskDto.setStatus("IN_PROGRESS");
        updatedTaskDto.setPriority("HIGH");

        mockMvc.perform(put("/api/tasks/" + taskId)
                        .cookie(new Cookie("accessToken", accessToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(updatedTaskDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Task"))
                .andExpect(jsonPath("$.description").value("Updated description"))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"))
                .andExpect(jsonPath("$.priority").value("HIGH"));
    }


    @Test
    public void testDeleteTask() throws Exception {
        TaskDto taskDto = new TaskDto();
        taskDto.setTitle("Task to Delete");
        taskDto.setDescription("Description");
        taskDto.setStatus("NEW");
        taskDto.setPriority("LOW");
        taskDto.setExecutorId(existingExecutorId);

        MvcResult result = mockMvc.perform(post("/api/tasks")
                        .cookie(new Cookie("accessToken", accessToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(taskDto)))
                .andExpect(status().isOk())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        Long taskId = JsonPath.parse(responseContent).read("$.id", Long.class);

        mockMvc.perform(delete("/api/tasks/" + taskId)
                        .cookie(new Cookie("accessToken", accessToken)))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/tasks/" + taskId)
                        .cookie(new Cookie("accessToken", accessToken)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetTasksWithPaginationAndFiltering() throws Exception {
        for (int i = 0; i < 5; i++) {
            TaskDto taskDto = new TaskDto();
            taskDto.setTitle("Task " + i);
            taskDto.setDescription("Description " + i);
            taskDto.setStatus(i % 2 == 0 ? "NEW" : "IN_PROGRESS");
            taskDto.setPriority(i % 2 == 0 ? "HIGH" : "LOW");
            taskDto.setExecutorId(existingExecutorId);

            mockMvc.perform(post("/api/tasks")
                            .cookie(new Cookie("accessToken", accessToken))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(taskDto)))
                    .andExpect(status().isOk());
        }

        mockMvc.perform(get("/api/tasks?status=NEW&page=0&size=2")
                        .cookie(new Cookie("accessToken", accessToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].status").value("NEW"));
    }


    @Test
    public void testAddCommentToTask() throws Exception {
        TaskDto taskDto = new TaskDto();
        taskDto.setTitle("Task with Comment");
        taskDto.setDescription("Task description");
        taskDto.setStatus("NEW");
        taskDto.setPriority("MEDIUM");
        taskDto.setExecutorId(existingExecutorId);

        MvcResult result = mockMvc.perform(post("/api/tasks")
                        .cookie(new Cookie("accessToken", accessToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(taskDto)))
                .andExpect(status().isOk())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        Long taskId = JsonPath.parse(responseContent).read("$.id", Long.class);

        CommentDto commentDto = new CommentDto();
        commentDto.setContent("This is a comment");
        commentDto.setTaskId(taskId);

        mockMvc.perform(post("/api/comments")
                        .cookie(new Cookie("accessToken", accessToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("This is a comment"));
    }


}
