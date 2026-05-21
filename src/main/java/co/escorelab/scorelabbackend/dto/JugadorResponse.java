package co.escorelab.scorelabbackend.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JugadorResponse {
    private Long id;
    private String nombre;
    private String documento;
    private String posicion;
    private Integer numeroCamiseta;
    private String nombreEquipo; // Útil para mostrar en el título si es necesario
}