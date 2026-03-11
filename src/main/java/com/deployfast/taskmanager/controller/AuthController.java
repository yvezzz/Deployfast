package com.deployfast.taskmanager.controller;

import com.deployfast.taskmanager.dto.request.LoginRequest;
import com.deployfast.taskmanager.dto.request.RegisterRequest;
import com.deployfast.taskmanager.dto.response.AuthResponse;
import com.deployfast.taskmanager.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
/**
 * Contrôleur pour les opérations d'authentification.
 * Ce contrôleur gère l'inscription et la connexion des utilisateurs.
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints de connexion et d'inscription")
public class AuthController {

    private final AuthService authService;

    /**
     * Inscription d'un nouvel utilisateur.
     *
     * @param request contient les informations de l'utilisateur (nom, email, mot de passe).
     * @return AuthResponse la réponse contenant le jeton JWT
     */
    @Operation(summary = "Inscrire un nouvel utilisateur", description = "Crée un nouveau compte utilisateur et retourne un jeton JWT de session.")
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return new ResponseEntity<>(authService.register(request), HttpStatus.CREATED);
    }

    /**
     * Connexion d'un utilisateur existant.
     *
     * @param request contient l'email et le mot de passe de l'utilisateur.
     * @return AuthResponse la réponse contenant le jeton JWT
     */
    @Operation(summary = "Se connecter", description = "Authentifie un utilisateur et retourne un jeton JWT.")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
