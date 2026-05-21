package co.escorelab.scorelabbackend.service;

import co.escorelab.scorelabbackend.dto.EquipoRequest;
import co.escorelab.scorelabbackend.dto.EquipoResponse;
import co.escorelab.scorelabbackend.model.Equipo;
import co.escorelab.scorelabbackend.model.Torneo;
import co.escorelab.scorelabbackend.model.Usuario;
import co.escorelab.scorelabbackend.repository.EquipoRepository;
import co.escorelab.scorelabbackend.repository.TorneoRepository;
import co.escorelab.scorelabbackend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EquipoService {

    private final EquipoRepository equipoRepository;
    private final UsuarioRepository usuarioRepository;
    private final TorneoRepository torneoRepository;

    /**
     * 🌟 NUEVO MÉTODO: Listar equipos por Torneo
     * Este método es el que llama el EquipoController para llenar los selects en el frontend.
     */
    public List<EquipoResponse> listarEquiposPorTorneo(Long torneoId) {
        // Buscamos el torneo para asegurar que existe
        Torneo torneo = torneoRepository.findById(torneoId)
                .orElseThrow(() -> new RuntimeException("Torneo no encontrado con ID: " + torneoId));

        // Buscamos los equipos vinculados a ese torneo
        return equipoRepository.findByTorneo(torneo).stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    /**
     * Crea un equipo vinculándolo al torneo seleccionado
     */
    @Transactional
    public EquipoResponse crearEquipo(EquipoRequest request, String correoUsuario) {
        Usuario delegado = usuarioRepository.findByCorreo(correoUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con correo: " + correoUsuario));

        Torneo torneo = torneoRepository.findById(request.getTorneoId())
                .orElseThrow(() -> new RuntimeException("El Torneo seleccionado (ID: " + request.getTorneoId() + ") no existe"));

        Equipo nuevoEquipo = Equipo.builder()
                .nombre(request.getNombre())
                .ciudad(request.getCiudad())
                .fechaCreacion(LocalDate.now())
                .delegado(delegado)
                .torneo(torneo)
                .estado("PENDIENTE")
                .build();

        return convertirAResponse(equipoRepository.save(nuevoEquipo));
    }

    public List<EquipoResponse> listarMisEquipos(String correoUsuario) {
        Usuario delegado = usuarioRepository.findByCorreo(correoUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return equipoRepository.findByDelegado(delegado).stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    public List<EquipoResponse> listarPendientes() {
        return equipoRepository.findByEstado("PENDIENTE").stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void cambiarEstado(Long equipoId, String nuevoEstado) {
        Equipo equipo = equipoRepository.findById(equipoId)
                .orElseThrow(() -> new RuntimeException("Equipo no encontrado"));

        equipo.setEstado(nuevoEstado.toUpperCase());
        equipoRepository.save(equipo);
    }

    private EquipoResponse convertirAResponse(Equipo equipo) {
        return EquipoResponse.builder()
                .id(equipo.getId())
                .nombre(equipo.getNombre())
                .ciudad(equipo.getCiudad())
                .fechaCreacion(equipo.getFechaCreacion())
                .nombreDelegado(equipo.getDelegado() != null ? equipo.getDelegado().getNombre() : "N/A")
                .estado(equipo.getEstado())
                .nombreTorneo(equipo.getTorneo() != null ? equipo.getTorneo().getNombre() : "Sin Torneo")
                .build();
    }
}