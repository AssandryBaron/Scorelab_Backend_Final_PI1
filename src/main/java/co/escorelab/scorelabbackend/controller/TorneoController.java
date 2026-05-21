package co.escorelab.scorelabbackend.controller;

import co.escorelab.scorelabbackend.dto.ApiResponse;
import co.escorelab.scorelabbackend.dto.TorneoRequest;
import co.escorelab.scorelabbackend.dto.TorneoResponse;
import co.escorelab.scorelabbackend.service.TorneoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/torneos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Permite que el frontend se conecte sin bloqueos de CORS
public class TorneoController {

    private final TorneoService torneoService;

    // POST /api/torneos -> Solo Organizador
    @PostMapping
    public ResponseEntity<ApiResponse<TorneoResponse>> crearTorneo(
            @Valid @RequestBody TorneoRequest request,
            Principal principal) {

        String correoUsuario = principal.getName();
        TorneoResponse response = torneoService.crearTorneo(request, correoUsuario);
        return ResponseEntity.ok(ApiResponse.ok("¡Torneo creado exitosamente!", response));
    }

    // GET /api/torneos/mis-torneos -> Solo Organizador
    @GetMapping("/mis-torneos")
    public ResponseEntity<ApiResponse<List<TorneoResponse>>> listarMisTorneos(Principal principal) {
        String correoUsuario = principal.getName();
        List<TorneoResponse> torneos = torneoService.listarTorneosDeOrganizador(correoUsuario);
        return ResponseEntity.ok(ApiResponse.ok("Tus torneos cargados con éxito", torneos));
    }

    /**
     * CORRECCIÓN AQUÍ:
     * Quitamos "/todos" para que la ruta sea simplemente GET /api/torneos
     * que es lo que tu Frontend está buscando.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<TorneoResponse>>> listarTodosLosTorneos() {
        List<TorneoResponse> torneos = torneoService.listarTodosLosTorneos();
        return ResponseEntity.ok(ApiResponse.ok("Lista de torneos obtenida", torneos));
    }
}