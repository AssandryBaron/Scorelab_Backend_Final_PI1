package co.escorelab.scorelabbackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EquipoRequest {

    @NotBlank(message = "El nombre del equipo es obligatorio")
    private String nombre;

    private String ciudad;

    // 🌟 Cambiamos a Long para recibir el ID del torneo
    @NotNull(message = "El ID del torneo es obligatorio")
    private Long torneoId;
}