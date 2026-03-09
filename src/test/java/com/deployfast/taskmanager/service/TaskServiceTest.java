package com.deployfast.taskmanager.service;

import com.deployfast.taskmanager.dto.request.TaskRequest;
import com.deployfast.taskmanager.dto.response.TaskResponse;
import com.deployfast.taskmanager.exception.ResourceNotFoundException;
import com.deployfast.taskmanager.model.Task;
import com.deployfast.taskmanager.model.User;
import com.deployfast.taskmanager.repository.TaskRepository;
import com.deployfast.taskmanager.service.impl.TaskServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskServiceImpl taskService;

    private User collabUser;
    private User adminUser;
    private Task task;

    @BeforeEach
    void setUp() {
        collabUser = User.builder().id(1L).email("collab@test.com").role("COLLABORATEUR").build();
        adminUser = User.builder().id(2L).email("admin@test.com").role("ADMIN").build();
        task = Task.builder().id(10L).title("Test Task").user(collabUser).createdAt(LocalDateTime.now()).build();
    }

    @Test
    void shouldCreateTaskSuccessfully() {
        TaskRequest request = new TaskRequest();
        request.setTitle("Task Test");
        request.setDescription("Desc Test");

        when(taskRepository.save(any(Task.class))).thenReturn(task);

        TaskResponse response = taskService.createTask(request, collabUser);

        assertNotNull(response);
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void shouldGetTaskByIdAsOwner() {
        when(taskRepository.findById(10L)).thenReturn(Optional.of(task));

        TaskResponse response = taskService.getTaskById(10L, collabUser);

        assertNotNull(response);
        assertEquals("Test Task", response.getTitle());
    }

    @Test
    void shouldGetTaskByIdAsAdmin() {
        when(taskRepository.findById(10L)).thenReturn(Optional.of(task));

        TaskResponse response = taskService.getTaskById(10L, adminUser);

        assertNotNull(response);
    }

    @Test
    void shouldThrowExceptionWhenTaskNotFound() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> taskService.getTaskById(99L, collabUser));
    }

    @Test
    void shouldThrowExceptionWhenNotOwner() {
        User otherUser = User.builder().id(3L).email("other@test.com").role("COLLABORATEUR").build();
        when(taskRepository.findById(10L)).thenReturn(Optional.of(task));

        assertThrows(ResourceNotFoundException.class, () -> taskService.getTaskById(10L, otherUser));
    }
}
