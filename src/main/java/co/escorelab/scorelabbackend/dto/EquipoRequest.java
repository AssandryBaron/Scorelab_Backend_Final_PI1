package co.escorelab.scorelabbackend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EquipoRequest {
    @NotBlank(message = "El nombre del equipo es obligatorio")
    private String nombre;

    private String ciudad;
}
