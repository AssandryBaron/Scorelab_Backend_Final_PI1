package co.escorelab.scorelabbackend.controller;

import co.escorelab.scorelabbackend.dto.ApiResponse;
import co.escorelab.scorelabbackend.dto.PlayerStatsDTO;
import co.escorelab.scorelabbackend.dto.StandingDTO;
import co.escorelab.scorelabbackend.service.StandingService;
import co.escorelab.scorelabbackend.service.StatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * EstadisticasController — Endpoints para el Módulo de Estadísticas del Torneo.
 * <p>Base path: {@code /api/estadisticas}
 */
@RestController
@RequestMapping("/api/estadisticas")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class EstadisticasController {

    private final StandingService standingService;
    private final StatService     statService;

    // ── Tabla de Posiciones ──────────────────────────────────────────────────

    /**
     * Retorna la tabla de posiciones ordenada de un torneo.
     * 🔓 PUBLICO: Se remueve @PreAuthorize para permitir que el frontend pinte la tabla libremente.
     * Se cambia a @RequestParam para que coincida con llamadas dinámicas (?torneoId=X)
     */
    @GetMapping("/posiciones")
    public ResponseEntity<ApiResponse<List<StandingDTO>>> getPosiciones(@RequestParam Long torneoId) {
        List<StandingDTO> tabla = standingService.calcularPosiciones(torneoId);
        return ResponseEntity.ok(ApiResponse.ok("Tabla de posiciones calculada exitosamente", tabla));
    }

    // ── Ranking de Goleadores ────────────────────────────────────────────────

    /**
     * Retorna el ranking de goleadores del torneo, ordenado por goles DESC.
     * 🔓 PUBLICO: Las estadísticas de goles usualmente también las quieren ver los visitantes.
     */
    @GetMapping("/goleadores")
    public ResponseEntity<ApiResponse<List<PlayerStatsDTO>>> getGoleadores(@RequestParam Long torneoId) {
        List<PlayerStatsDTO> ranking = statService.getRankingGoleadores(torneoId);
        return ResponseEntity.ok(ApiResponse.ok("Ranking de goleadores calculado exitosamente", ranking));
    }

    // ── Control Disciplinario ────────────────────────────────────────────────

    /**
     * Retorna la lista de jugadores amonestados del torneo.
     * 🔓 PUBLICO: Tarjetas amarillas y rojas del torneo.
     */
    @GetMapping("/disciplina")
    public ResponseEntity<ApiResponse<List<PlayerStatsDTO>>> getDisciplina(@RequestParam Long torneoId) {
        List<PlayerStatsDTO> disciplina = statService.getControlDisciplinario(torneoId);
        return ResponseEntity.ok(ApiResponse.ok("Control disciplinario calculado exitosamente", disciplina));
    }
}