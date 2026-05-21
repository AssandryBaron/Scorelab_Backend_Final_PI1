package co.escorelab.scorelabbackend.dto;

import co.escorelab.scorelabbackend.model.EstadoPartido;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class PartidoResponse {
    private Long id;
    private String equipoLocal;
    private String equipoVisitante;
    private Integer golesLocal;
    private Integer golesVisitante;
    private LocalDateTime fechaHora;
    private String lugar;
    private EstadoPartido estado;
}