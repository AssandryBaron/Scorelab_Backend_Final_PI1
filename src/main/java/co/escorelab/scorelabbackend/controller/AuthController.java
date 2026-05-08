package co.escorelab.scorelabbackend.controller;

import co.escorelab.scorelabbackend.dto.ApiResponse;
import co.escorelab.scorelabbackend.dto.AuthResponse;
import co.escorelab.scorelabbackend.dto.LoginRequest;
import co.escorelab.scorelabbackend.dto.RegistroRequest;
import co.escorelab.scorelabbackend.model.Usuario;
import co.escorelab.scorelabbackend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<Usuario> registrar(@Valid @RequestBody RegistroRequest request) {
        return ResponseEntity.ok(authService.registrar(request));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request.getCorreo(), request.getContrasena());
        return ResponseEntity.ok(ApiResponse.ok("Inicio de sesión exitoso", response));
    }
}
