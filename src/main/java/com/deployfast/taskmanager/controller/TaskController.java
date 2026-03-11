package com.deployfast.taskmanager.controller;

import com.deployfast.taskmanager.dto.request.TaskRequest;
import com.deployfast.taskmanager.dto.response.TaskResponse;
import com.deployfast.taskmanager.model.User;
import com.deployfast.taskmanager.repository.UserRepository;
import com.deployfast.taskmanager.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

/**
 * Contrôleur final pour les opérations sur les tâches.
 */
@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
@Tag(name = "Tasks", description = "Endpoints de gestion des tâches (CRUD)")
@SecurityRequirement(name = "bearerAuth")
public class TaskController {

    private final TaskService taskService;
    private final UserRepository userRepository;

    /**
     * Récupère toutes les tâches de l'utilisateur avec pagination.
     *
     * @param status Statut optionnel pour le filtrage
     * @param pageable Les options de pagination
     * @param userDetails Les informations de l'utilisateur connecté
     * @return Une page de tâches correspondant aux critères
     */
    @Operation(summary = "Lister les tâches", description = "Récupère toutes les tâches de l'utilisateur avec une pagination et filtre optionnel.")
    @GetMapping
    public ResponseEntity<Page<TaskResponse>> getAllTasks(
            @RequestParam(required = false) Boolean status,
            @PageableDefault(size = 10) Pageable pageable,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        return ResponseEntity.ok(taskService.getAllTasks(user, status, pageable)); 
    }

    /**
     * Crée une nouvelle tâche pour l'utilisateur.
     *
     * @param request La définition de la tâche à créer
     * @param userDetails Les informations de l'utilisateur connecté
     * @return La tâche nouvellement créée
     */
    @Operation(summary = "Créer une tâche", description = "Crée une nouvelle tâche pour l'utilisateur connecté.")
    @PostMapping
    public ResponseEntity<TaskResponse> createTask(
            @Valid @RequestBody TaskRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        return new ResponseEntity<>(taskService.createTask(request, user), HttpStatus.CREATED);
    }

    /**
     * Récupère une tâche spécifique par son identifiant.
     *
     * @param id L'identifiant de la tâche
     * @param userDetails Les informations de l'utilisateur connecté
     * @return La tâche recherchée
     */
    @Operation(summary = "Récupérer une tâche", description = "Récupère le détail d'une tâche via son identifiant.")
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTaskById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        return ResponseEntity.ok(taskService.getTaskById(id, user));
    }

    /**
     * Met à jour une tâche existante.
     *
     * @param id L'identifiant de la tâche à modifier
     * @param request Les nouvelles informations pour cette tâche
     * @param userDetails Les informations de l'utilisateur connecté
     * @return La tâche mise à jour
     */
    @Operation(summary = "Mettre à jour une tâche", description = "Modifie les attributs d'une tâche existante de l'utilisateur.")
    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable Long id, 
            @Valid @RequestBody TaskRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        return ResponseEntity.ok(taskService.updateTask(id, request, user));
    }

    /**
     * Supprime une tâche existante.
     *
     * @param id L'identifiant de la tâche à supprimer
     * @param userDetails Les informations de l'utilisateur connecté
     */
    @Operation(summary = "Supprimer une tâche", description = "Supprime de façon permanente une tâche.")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        taskService.deleteTask(id, user);
    }
}
