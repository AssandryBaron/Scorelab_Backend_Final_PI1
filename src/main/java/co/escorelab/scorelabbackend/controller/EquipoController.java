package co.escorelab.scorelabbackend.controller;

import co.escorelab.scorelabbackend.dto.ApiResponse;
import co.escorelab.scorelabbackend.dto.EquipoRequest;
import co.escorelab.scorelabbackend.dto.EquipoResponse;
import co.escorelab.scorelabbackend.service.EquipoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/equipos")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PATCH, RequestMethod.OPTIONS})
public class EquipoController {

    private final EquipoService equipoService;

    /**
     * 🌟 NUEVO MÉTODO: Listar equipos por Torneo
     * GET /api/equipos/torneo/{id}
     * Fundamental para la Gestión de Partidos
     */
    @GetMapping("/torneo/{torneoId}")
    @PreAuthorize("hasAnyAuthority('ORGANIZADOR', 'ROLE_ORGANIZADOR', 'DELEGADO', 'ROLE_DELEGADO')")
    public ResponseEntity<ApiResponse<List<EquipoResponse>>> listarPorTorneo(@PathVariable Long torneoId) {
        System.out.println("🔍 Consultando equipos para el torneo ID: " + torneoId);
        List<EquipoResponse> equipos = equipoService.listarEquiposPorTorneo(torneoId);
        return ResponseEntity.ok(ApiResponse.ok("Equipos del torneo cargados", equipos));
    }

    /**
     * Inscribir un nuevo equipo
     */
    @PostMapping("/inscribir")
    @PreAuthorize("hasAnyAuthority('DELEGADO', 'ROLE_DELEGADO')")
    public ResponseEntity<ApiResponse<EquipoResponse>> crearEquipo(
            @Valid @RequestBody EquipoRequest request,
            Principal principal) {
        String correoUsuario = principal.getName();
        EquipoResponse response = equipoService.crearEquipo(request, correoUsuario);
        return ResponseEntity.ok(ApiResponse.ok("¡Equipo inscrito con éxito!", response));
    }

    /**
     * Listar equipos del delegado logueado
     */
    @GetMapping("/mis-equipos")
    @PreAuthorize("hasAnyAuthority('DELEGADO', 'ROLE_DELEGADO')")
    public ResponseEntity<ApiResponse<List<EquipoResponse>>> listarMisEquipos(Principal principal) {
        String correoUsuario = principal.getName();
        List<EquipoResponse> equipos = equipoService.listarMisEquipos(correoUsuario);
        return ResponseEntity.ok(ApiResponse.ok("Tus equipos han sido cargados", equipos));
    }

    /**
     * Listar solicitudes pendientes (Solo Organizador)
     */
    @GetMapping("/solicitudes")
    @PreAuthorize("hasAnyAuthority('ORGANIZADOR', 'ROLE_ORGANIZADOR')")
    public ResponseEntity<ApiResponse<List<EquipoResponse>>> listarPendientes() {
        List<EquipoResponse> pendientes = equipoService.listarPendientes();
        return ResponseEntity.ok(ApiResponse.ok("Solicitudes cargadas", pendientes));
    }

    /**
     * Aprobar equipo (Solo Organizador)
     */
    @PatchMapping("/{id}/aprobar")
    @PreAuthorize("hasAnyAuthority('ORGANIZADOR', 'ROLE_ORGANIZADOR')")
    public ResponseEntity<ApiResponse<Void>> aprobarEquipo(@PathVariable Long id) {
        equipoService.cambiarEstado(id, "APROBADO");
        return ResponseEntity.ok(ApiResponse.ok("Equipo aprobado exitosamente", null));
    }
}