package co.escorelab.scorelabbackend.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PartidoRequest {
    private Long torneoId;
    private Long localId;
    private Long visitanteId;
    private LocalDateTime fechaHora;
    private String lugar;
}