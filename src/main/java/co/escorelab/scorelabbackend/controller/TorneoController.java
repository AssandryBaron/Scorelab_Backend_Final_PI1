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
public class TorneoController {

    private final TorneoService torneoService;

    // 🔒 Protegido: Crear torneo
    @PostMapping
    public ResponseEntity<ApiResponse<TorneoResponse>> crearTorneo(
            @Valid @RequestBody TorneoRequest request,
            Principal principal) {

        String correoUsuario = principal.getName();
        TorneoResponse response = torneoService.crearTorneo(request, correoUsuario);
        return ResponseEntity.ok(ApiResponse.ok("¡Torneo creado exitosamente!", response));
    }

    // 🔒 Protegido: Ver solo MIS torneos
    @GetMapping("/mis-torneos")
    public ResponseEntity<ApiResponse<List<TorneoResponse>>> listarMisTorneos(Principal principal) {
        String correoUsuario = principal.getName();
        List<TorneoResponse> torneos = torneoService.listarTorneosDeOrganizador(correoUsuario);
        return ResponseEntity.ok(ApiResponse.ok("Tus torneos cargados con éxito", torneos));
    }

    // 🔓 Público: Ver TODOS los torneos del sistema
    @GetMapping("/todos")
    public ResponseEntity<ApiResponse<List<TorneoResponse>>> listarTodosLosTorneos() {
        List<TorneoResponse> torneos = torneoService.listarTodosLosTorneos();
        return ResponseEntity.ok(ApiResponse.ok("Lista de torneos obtenida", torneos));
    }
}
