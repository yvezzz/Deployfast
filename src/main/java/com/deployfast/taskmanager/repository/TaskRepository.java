package com.deployfast.taskmanager.repository;

import com.deployfast.taskmanager.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


/**
 * Interface pour l'accès aux données des tâches.
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    Page<Task> findByUserId(Long userId, Pageable pageable);
    Page<Task> findByUserIdAndStatus(Long userId, boolean status, Pageable pageable);
    Page<Task> findByStatus(boolean status, Pageable pageable); // Pour ADMIN/RH
}
