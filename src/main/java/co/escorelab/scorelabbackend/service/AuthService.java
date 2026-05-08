package co.escorelab.scorelabbackend.service;

import co.escorelab.scorelabbackend.dto.AuthResponse;
import co.escorelab.scorelabbackend.dto.RegistroRequest;
import co.escorelab.scorelabbackend.model.Usuario;
import co.escorelab.scorelabbackend.repository.UsuarioRepository;
import co.escorelab.scorelabbackend.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService; // 🌟 Inyectado correctamente
    private final BCryptPasswordEncoder passwordEncoder; // 🌟 Inyectado desde SecurityConfig

    public Usuario registrar(RegistroRequest request) {
        // 1. Verificar si el correo ya existe
        if (usuarioRepository.findByCorreo(request.getCorreo()).isPresent()) {
            throw new RuntimeException("El correo ya está registrado");
        }

        // 2. Crear el nuevo usuario (el rol viene del request)
        Usuario nuevoUsuario = Usuario.builder()
                .nombre(request.getNombre())
                .correo(request.getCorreo())
                .contrasena(passwordEncoder.encode(request.getContrasena()))
                .rol(request.getRol())
                .build();

        return usuarioRepository.save(nuevoUsuario);
    }

    public AuthResponse login(String correo, String contrasena) {
        // 1. Buscar usuario
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 2. Validar contraseña
        if (passwordEncoder.matches(contrasena, usuario.getContrasena())) {

            // 3. Generar token incluyendo el ROL (usando el JwtService que corregimos antes)
            String token = jwtService.generarToken(usuario.getCorreo(), usuario.getRol().name());

            // 4. Devolvemos objeto completo para que React sepa qué hacer
            return new AuthResponse(token, usuario.getRol().name(), usuario.getNombre());

        } else {
            throw new RuntimeException("Contraseña incorrecta");
        }
    }
}