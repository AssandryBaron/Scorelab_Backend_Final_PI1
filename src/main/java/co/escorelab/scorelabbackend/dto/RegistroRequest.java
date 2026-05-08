package co.escorelab.scorelabbackend.dto;

import co.escorelab.scorelabbackend.model.Rol;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegistroRequest {
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @Email(message = "Correo no válido")
    @NotBlank(message = "El correo es obligatorio")
    private String correo;

    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String contrasena;

    @NotNull(message = "El rol es obligatorio")
    private Rol rol;
}
