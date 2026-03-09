package com.deployfast.taskmanager.service;

import com.deployfast.taskmanager.dto.request.TaskRequest;
import com.deployfast.taskmanager.dto.response.TaskResponse;
import com.deployfast.taskmanager.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Interface pour les services de gestion des tâches.
 */
public interface TaskService {
    TaskResponse createTask(TaskRequest request, User user);
    Page<TaskResponse> getAllTasks(User user, Boolean status, Pageable pageable);
    TaskResponse getTaskById(Long id, User user);
    TaskResponse updateTask(Long id, TaskRequest request, User user);
    void deleteTask(Long id, User user);
}
