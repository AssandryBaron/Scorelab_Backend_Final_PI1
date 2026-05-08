package co.escorelab.scorelabbackend.controller;

import co.escorelab.scorelabbackend.dto.ApiResponse;
import co.escorelab.scorelabbackend.dto.EquipoRequest;
import co.escorelab.scorelabbackend.dto.EquipoResponse;
import co.escorelab.scorelabbackend.service.EquipoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/equipos")
@RequiredArgsConstructor
public class EquipoController {

    private final EquipoService equipoService;

    // Crear un nuevo equipo
    @PostMapping
    public ResponseEntity<ApiResponse<EquipoResponse>> crearEquipo(
            @Valid @RequestBody EquipoRequest request,
            Principal principal) {

        String correoUsuario = principal.getName();
        EquipoResponse response = equipoService.crearEquipo(request, correoUsuario);
        return ResponseEntity.ok(ApiResponse.ok("¡Equipo creado exitosamente!", response));
    }

    //Ver los equipos que yo administro
    @GetMapping("/mis-equipos")
    public ResponseEntity<ApiResponse<List<EquipoResponse>>> listarMisEquipos(Principal principal) {
        String correoUsuario = principal.getName();
        List<EquipoResponse> equipos = equipoService.listarMisEquipos(correoUsuario);
        return ResponseEntity.ok(ApiResponse.ok("Tus equipos cargados con éxito", equipos));
    }

    // Obtener solicitudes pendientes (Para el Organizador)
    @GetMapping("/pendientes")
    public ResponseEntity<ApiResponse<List<EquipoResponse>>> listarPendientes() {
        List<EquipoResponse> pendientes = equipoService.listarPendientes();
        return ResponseEntity.ok(ApiResponse.ok("Solicitudes de inscripción", pendientes));
    }

    //Solo el Organizador puede aprobar equipos
    @PatchMapping("/{id}/aprobar")
    public ResponseEntity<ApiResponse<Void>> aprobarEquipo(@PathVariable Long id) {
        equipoService.cambiarEstado(id, "APROBADO");
        return ResponseEntity.ok(ApiResponse.ok("Equipo aprobado con éxito", null));
    }
}
