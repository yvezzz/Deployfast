package com.deployfast.taskmanager.controller;

import com.deployfast.taskmanager.dto.request.TaskRequest;
import com.deployfast.taskmanager.dto.response.TaskResponse;
import com.deployfast.taskmanager.model.User;
import com.deployfast.taskmanager.repository.UserRepository;
import com.deployfast.taskmanager.service.TaskService;
import com.deployfast.taskmanager.config.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.authentication.AuthenticationProvider;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
@AutoConfigureMockMvc(addFilters = false)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaskService taskService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private AuthenticationProvider authenticationProvider;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private TaskResponse testTaskResponse;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("user@example.com");

        testTaskResponse = new TaskResponse();
        testTaskResponse.setId(1L);
        testTaskResponse.setTitle("Test Task");
        testTaskResponse.setDescription("Description");
        testTaskResponse.setStatus(false);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
    }

    @Test
    @WithMockUser(username = "user@example.com")
    void getAllTasksShouldReturnPage() throws Exception {
        Page<TaskResponse> page = new PageImpl<>(Collections.singletonList(testTaskResponse));
        when(taskService.getAllTasks(any(User.class), any(), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Test Task"));
    }

    @Test
    @WithMockUser(username = "user@example.com")
    void createTaskShouldReturnCreated() throws Exception {
        TaskRequest request = new TaskRequest();
        request.setTitle("New Task");
        request.setStatus(false);

        when(taskService.createTask(any(TaskRequest.class), any(User.class))).thenReturn(testTaskResponse);

        mockMvc.perform(post("/api/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Test Task"));
    }

    @Test
    @WithMockUser(username = "user@example.com")
    void getTaskByIdShouldReturnTask() throws Exception {
        when(taskService.getTaskById(anyLong(), any(User.class))).thenReturn(testTaskResponse);

        mockMvc.perform(get("/api/v1/tasks/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Task"));
    }

    @Test
    @WithMockUser(username = "user@example.com")
    void updateTaskShouldReturnUpdatedTask() throws Exception {
        TaskRequest request = new TaskRequest();
        request.setTitle("Updated Task");
        
        when(taskService.updateTask(anyLong(), any(TaskRequest.class), any(User.class))).thenReturn(testTaskResponse);

        mockMvc.perform(put("/api/v1/tasks/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Task"));
    }

    @Test
    @WithMockUser(username = "user@example.com")
    void deleteTaskShouldReturnNoContent() throws Exception {
        doNothing().when(taskService).deleteTask(anyLong(), any(User.class));

        mockMvc.perform(delete("/api/v1/tasks/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}
