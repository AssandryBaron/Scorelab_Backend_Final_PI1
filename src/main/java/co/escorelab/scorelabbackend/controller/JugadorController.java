package co.escorelab.scorelabbackend.controller;

import co.escorelab.scorelabbackend.dto.ApiResponse;
import co.escorelab.scorelabbackend.dto.JugadorRequest;
import co.escorelab.scorelabbackend.dto.JugadorResponse;
import co.escorelab.scorelabbackend.service.JugadorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/jugadores")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class JugadorController {

    private final JugadorService jugadorService;

    /**
     * 📊 NUEVO: Obtiene la base de datos completa de jugadores.
     * Ideal para el panel del ORGANIZADOR.
     */
    @GetMapping("/todos")
    @PreAuthorize("hasAnyAuthority('ORGANIZADOR', 'ROLE_ORGANIZADOR')")
    public ResponseEntity<ApiResponse<List<JugadorResponse>>> obtenerTodosLosJugadores() {
        List<JugadorResponse> responses = jugadorService.listarTodosLosJugadores()
                .stream()
                .map(this::convertirADto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.ok("Base de datos de jugadores obtenida", responses));
    }

    // 🌟 Listar jugadores de un equipo específico
    @GetMapping("/equipo/{equipoId}")
    public ResponseEntity<ApiResponse<List<JugadorResponse>>> listarJugadores(@PathVariable Long equipoId) {
        List<JugadorResponse> responses = jugadorService.listarJugadoresPorEquipo(equipoId)
                .stream()
                .map(this::convertirADto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.ok("Lista de jugadores obtenida", responses));
    }

    // Para registrar uno por uno (Solo DELEGADOS)
    @PostMapping("/registrar/{equipoId}")
    @PreAuthorize("hasAnyAuthority('DELEGADO', 'ROLE_DELEGADO')")
    public ResponseEntity<ApiResponse<JugadorResponse>> registrarJugador(
            @PathVariable Long equipoId,
            @Valid @RequestBody JugadorRequest request,
            Principal principal) {

        String correoDelegado = principal.getName();
        JugadorResponse response = convertirADto(jugadorService.registrarJugador(equipoId, request, correoDelegado));
        return ResponseEntity.ok(ApiResponse.ok("¡Jugador registrado con éxito!", response));
    }

    // 🌟 Guardar en lote (Solo DELEGADOS)
    @PostMapping("/lote/{equipoId}")
    @PreAuthorize("hasAnyAuthority('DELEGADO', 'ROLE_DELEGADO')")
    public ResponseEntity<ApiResponse<List<JugadorResponse>>> registrarJugadoresLote(
            @PathVariable Long equipoId,
            @Valid @RequestBody List<JugadorRequest> requests,
            Principal principal) {

        String correoDelegado = principal.getName();
        List<JugadorResponse> responses = jugadorService.registrarJugadoresLote(equipoId, requests, correoDelegado)
                .stream()
                .map(this::convertirADto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.ok("¡Plantilla registrada con éxito!", responses));
    }

    /**
     * Mapeador interno para transformar la entidad en DTO de respuesta
     */
    private JugadorResponse convertirADto(co.escorelab.scorelabbackend.model.Jugador j) {
        return JugadorResponse.builder()
                .id(j.getId())
                .nombre(j.getNombre())
                .documento(j.getDocumento())
                .posicion(j.getPosicion())
                .numeroCamiseta(j.getNumeroCamiseta())
                .nombreEquipo(j.getEquipo() != null ? j.getEquipo().getNombre() : "Sin Equipo")
                .build();
    }
}