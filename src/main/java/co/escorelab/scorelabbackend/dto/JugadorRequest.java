package co.escorelab.scorelabbackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class JugadorRequest {
    @NotBlank(message = "El nombre del jugador es obligatorio")
    private String nombre;

    @NotBlank(message = "El documento es obligatorio")
    private String documento;

    private String posicion;

    @NotNull(message = "El número de camiseta es obligatorio")
    private Integer numeroCamiseta;
}
