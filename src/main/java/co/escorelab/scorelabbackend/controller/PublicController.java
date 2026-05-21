package co.escorelab.scorelabbackend.controller;

import co.escorelab.scorelabbackend.dto.ApiResponse;
import co.escorelab.scorelabbackend.dto.PlayerStatsDTO;
import co.escorelab.scorelabbackend.dto.StandingDTO;
import co.escorelab.scorelabbackend.dto.TorneoResponse;
import co.escorelab.scorelabbackend.model.Partido;
import co.escorelab.scorelabbackend.service.StandingService;
import co.escorelab.scorelabbackend.service.StatService;
import co.escorelab.scorelabbackend.service.TorneoService;
import co.escorelab.scorelabbackend.service.PartidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * PublicController — Endpoints PÚBLICOS para el Panel del Espectador.
 * Base path: /api/public
 */
@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class PublicController {

    private final TorneoService   torneoService;
    private final PartidoService  partidoService;
    private final StandingService standingService;
    private final StatService     statService;

    // ── Torneos ──────────────────────────────────────────────────────────────

    @GetMapping("/torneos")
    public ResponseEntity<ApiResponse<List<TorneoResponse>>> getTorneos() {
        List<TorneoResponse> torneos = torneoService.listarTodosLosTorneos();
        // Solución: Usamos tu método estático .ok() que infiere el tipo List<TorneoResponse> perfectamente
        return ResponseEntity.ok(ApiResponse.ok("Torneos disponibles", torneos));
    }

    // ── Calendario / Resultados ───────────────────────────────────────────────

    @GetMapping("/torneos/{torneoId}/partidos")
    public ResponseEntity<ApiResponse<List<Partido>>> getPartidos(@PathVariable Long torneoId) {
        List<Partido> partidos = partidoService.listarPorTorneo(torneoId);
        return ResponseEntity.ok(ApiResponse.ok("Calendario de partidos", partidos));
    }

    // ── Tabla de Posiciones ──────────────────────────────────────────────────

    @GetMapping("/torneos/{torneoId}/posiciones")
    public ResponseEntity<ApiResponse<List<StandingDTO>>> getPosiciones(@PathVariable Long torneoId) {
        List<StandingDTO> tabla = standingService.calcularPosiciones(torneoId);
        return ResponseEntity.ok(ApiResponse.ok("Tabla de posiciones", tabla));
    }

    // ── Ranking de Goleadores ────────────────────────────────────────────────

    @GetMapping("/torneos/{torneoId}/goleadores")
    public ResponseEntity<ApiResponse<List<PlayerStatsDTO>>> getGoleadores(@PathVariable Long torneoId) {
        List<PlayerStatsDTO> ranking = statService.getRankingGoleadores(torneoId);
        return ResponseEntity.ok(ApiResponse.ok("Ranking de goleadores", ranking));
    }

    // ── Control Disciplinario ────────────────────────────────────────────────

    @GetMapping("/torneos/{torneoId}/disciplina")
    public ResponseEntity<ApiResponse<List<PlayerStatsDTO>>> getDisciplina(@PathVariable Long torneoId) {
        List<PlayerStatsDTO> disciplina = statService.getControlDisciplinario(torneoId);
        return ResponseEntity.ok(ApiResponse.ok("Control disciplinario", disciplina));
    }
}