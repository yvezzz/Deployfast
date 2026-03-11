package com.deployfast.taskmanager.service.impl;

import com.deployfast.taskmanager.dto.request.TaskRequest;
import com.deployfast.taskmanager.dto.response.TaskResponse;
import com.deployfast.taskmanager.exception.ResourceNotFoundException;
import com.deployfast.taskmanager.model.Task;
import com.deployfast.taskmanager.model.User;
import com.deployfast.taskmanager.repository.TaskRepository;
import com.deployfast.taskmanager.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implémentation du service de gestion des tâches.
 */
@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    @Override
    @Transactional
    public TaskResponse createTask(TaskRequest request, User user) {
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(request.isStatus());
        task.setUser(user);

        Task savedTask = taskRepository.save(task);
        return mapToResponse(savedTask);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TaskResponse> getAllTasks(User user, Boolean status, Pageable pageable) {
        Page<Task> tasks;
        // Si l'utilisateur est ADMIN ou RH, il voit toutes les tâches
        if ("ADMIN".equals(user.getRole()) || "RH".equals(user.getRole())) {
            if (status != null) {
                tasks = taskRepository.findByStatus(status, pageable);
            } else {
                tasks = taskRepository.findAll(pageable);
            }
        } else {
            // Collaborateur : ne voit que ses propres tâches
            if (status != null) {
                tasks = taskRepository.findByUserIdAndStatus(user.getId(), status, pageable);
            } else {
                tasks = taskRepository.findByUserId(user.getId(), pageable);
            }
        }
        return tasks.map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public TaskResponse getTaskById(Long id, User user) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tâche non trouvée"));

        // Vérification de propriété (sauf pour ADMIN/RH)
        if (!"ADMIN".equals(user.getRole()) && !"RH".equals(user.getRole()) && !task.getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Accès refusé");
        }
        return mapToResponse(task);
    }

    @Override
    @Transactional
    public TaskResponse updateTask(Long id, TaskRequest request, User user) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tâche non trouvée"));

        // Seul l'ADMIN ou le propriétaire peut modifier
        if (!"ADMIN".equals(user.getRole()) && !task.getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Accès refusé");
        }

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(request.isStatus());

        Task updatedTask = taskRepository.save(task);
        return mapToResponse(updatedTask);
    }

    @Override
    @Transactional
    public void deleteTask(Long id, User user) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tâche non trouvée"));

        // Seul l'ADMIN ou le propriétaire peut supprimer
        if (!"ADMIN".equals(user.getRole()) && !task.getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Accès refusé");
        }
        taskRepository.delete(task);
    }

    private TaskResponse mapToResponse(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.isStatus())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }
}
