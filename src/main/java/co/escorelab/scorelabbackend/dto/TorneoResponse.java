package co.escorelab.scorelabbackend.dto;

import co.escorelab.scorelabbackend.model.EstadoTorneo;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Data
@Builder
public class TorneoResponse {
    private Long id;
    private String nombre;
    private String descripcion;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private EstadoTorneo estado; // El estado dinámico
    private String nombreOrganizador;
    private int cantidadEquipos;
}
