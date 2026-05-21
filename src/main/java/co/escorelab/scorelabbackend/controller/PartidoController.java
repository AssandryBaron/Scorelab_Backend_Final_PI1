package co.escorelab.scorelabbackend.controller;

import co.escorelab.scorelabbackend.dto.ApiResponse;
import co.escorelab.scorelabbackend.dto.PartidoRequest;
import co.escorelab.scorelabbackend.model.Partido;
import co.escorelab.scorelabbackend.service.PartidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/partidos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PartidoController {

    private final PartidoService partidoService;

    @GetMapping("/{partidoId}")
    public ResponseEntity<ApiResponse<Partido>> obtenerPorId(@PathVariable Long partidoId) {
        return ResponseEntity.ok(ApiResponse.ok("Partido encontrado", partidoService.buscarPorId(partidoId)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Partido>>> listarTodos() {
        return ResponseEntity.ok(ApiResponse.ok("Calendario obtenido", partidoService.listarTodos()));
    }

    /**
     * ⚽ Obtiene los partidos de un torneo específico.
     * Se usa en la vista "Gestión de Partidos" del Organizador.
     */
    @GetMapping("/torneo/{torneoId}")
    public ResponseEntity<ApiResponse<List<Partido>>> listarPorTorneo(@PathVariable Long torneoId) {
        // Se cambió a obtenerPartidosPorTorneo para coincidir con tu PartidoService
        List<Partido> partidos = partidoService.listarPorTorneo(torneoId);
        return ResponseEntity.ok(ApiResponse.ok("Partidos del torneo cargados", partidos));
    }

    @PostMapping("/{partidoId}/finalizar")
    @PreAuthorize("hasAnyAuthority('ORGANIZADOR', 'ROLE_ORGANIZADOR')")
    public ResponseEntity<ApiResponse<Partido>> finalizar(
            @PathVariable Long partidoId,
            @RequestBody Map<String, Integer> marcador) {

        Partido p = partidoService.registrarResultado(
                partidoId,
                marcador.get("golesLocal"),
                marcador.get("golesVisitante")
        );
        return ResponseEntity.ok(ApiResponse.ok("Partido finalizado correctamente", p));
    }

    @PostMapping("/programar")
    @PreAuthorize("hasAnyAuthority('ORGANIZADOR', 'ROLE_ORGANIZADOR')")
    public ResponseEntity<ApiResponse<Partido>> programar(@RequestBody PartidoRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("Partido programado", partidoService.programarPartido(req)));
    }

    @DeleteMapping("/{partidoId}")
    @PreAuthorize("hasAnyAuthority('ORGANIZADOR', 'ROLE_ORGANIZADOR')")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long partidoId) {
        partidoService.eliminarPartido(partidoId);
        return ResponseEntity.ok(ApiResponse.ok("Partido eliminado", null));
    }
}