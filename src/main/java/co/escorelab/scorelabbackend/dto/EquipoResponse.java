package co.escorelab.scorelabbackend.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Data
@Builder
public class EquipoResponse {
    private Long id;
    private String nombre;
    private String ciudad;
    private LocalDate fechaCreacion;
    private String nombreDelegado;
    private String estado;
    private String nombreTorneo;
}
