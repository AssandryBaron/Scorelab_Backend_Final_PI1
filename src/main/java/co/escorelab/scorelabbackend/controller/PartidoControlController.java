package co.escorelab.scorelabbackend.controller;

import co.escorelab.scorelabbackend.dto.ApiResponse;
import co.escorelab.scorelabbackend.model.EventoPartido;
import co.escorelab.scorelabbackend.model.TipoEvento;
import co.escorelab.scorelabbackend.service.PartidoService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/control-partido")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PartidoControlController {

    private final PartidoService partidoService;

    /**
     * ⚽ GUARDA LA PLANTILLA (Corregido para evitar Error 400)
     * Se utiliza PlantillaRequest para mapear correctamente el JSON del frontend.
     */
    @PostMapping("/{partidoId}/plantilla")
    @PreAuthorize("hasAnyAuthority('ORGANIZADOR', 'ROLE_ORGANIZADOR')")
    public ResponseEntity<ApiResponse<Void>> guardarPlantilla(
            @PathVariable Long partidoId,
            @RequestBody PlantillaRequest request) { // <--- Cambiado de List<Long> a DTO

        partidoService.guardarPlantilla(partidoId, request.getJugadorIds());
        return ResponseEntity.ok(ApiResponse.ok("Plantilla guardada correctamente", null));
    }

    @PostMapping("/{partidoId}/evento")
    @PreAuthorize("hasAnyAuthority('ORGANIZADOR', 'ROLE_ORGANIZADOR')")
    public ResponseEntity<ApiResponse<EventoPartido>> registrarEvento(
            @PathVariable Long partidoId,
            @RequestBody EventoRequestDTO req) {

        EventoPartido ev = partidoService.registrarEvento(
                partidoId,
                req.getJugadorId(),
                req.getMinuto(),
                req.getTipo()
        );
        return ResponseEntity.ok(ApiResponse.ok("Evento registrado", ev));
    }

    @GetMapping("/{partidoId}/cronologia")
    public ResponseEntity<ApiResponse<List<EventoPartido>>> obtenerCronologia(@PathVariable Long partidoId) {
        List<EventoPartido> eventos = partidoService.obtenerCronologia(partidoId);
        return ResponseEntity.ok(ApiResponse.ok("Cronología obtenida", eventos));
    }
}

/**
 * DTO para recibir la lista de jugadores titulares.
 */
@Data
class PlantillaRequest {
    private List<Long> jugadorIds;
}

@Data
class EventoRequestDTO {
    private Long jugadorId;
    private Integer minuto;
    private TipoEvento tipo;
}