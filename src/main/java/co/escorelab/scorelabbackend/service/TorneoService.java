package co.escorelab.scorelabbackend.service;

import co.escorelab.scorelabbackend.dto.TorneoRequest;
import co.escorelab.scorelabbackend.dto.TorneoResponse;
import co.escorelab.scorelabbackend.model.Torneo;
import co.escorelab.scorelabbackend.model.Usuario;
import co.escorelab.scorelabbackend.repository.EquipoRepository;
import co.escorelab.scorelabbackend.repository.TorneoRepository;
import co.escorelab.scorelabbackend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TorneoService {

    private final TorneoRepository torneoRepository;
    private final UsuarioRepository usuarioRepository;
    private final EquipoRepository equipoRepository;

    public TorneoResponse crearTorneo(TorneoRequest request, String correoUsuario) {
        // 1. Buscamos al usuario dueño del token
        Usuario organizador = usuarioRepository.findByCorreo(correoUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 2. Creamos la entidad Torneo
        Torneo nuevoTorneo = Torneo.builder()
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .fechaInicio(request.getFechaInicio())
                .fechaFin(request.getFechaFin())
                .organizador(organizador)
                .build();

        // 3. Lo guardamos en la base de datos
        Torneo torneoGuardado = torneoRepository.save(nuevoTorneo);

        // 4. Devolvemos la respuesta mapeada
        return convertirAResponse(torneoGuardado);
    }

    public List<TorneoResponse> listarTorneosDeOrganizador(String correoUsuario) {
        Usuario organizador = usuarioRepository.findByCorreo(correoUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return torneoRepository.findByOrganizador(organizador).stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    public List<TorneoResponse> listarTodosLosTorneos() {
        return torneoRepository.findAll().stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    private TorneoResponse convertirAResponse(Torneo torneo) {
        // Consulta directa a la BD filtrando por Torneo ID y estado APROBADO
        long contadorReal = equipoRepository.countByTorneoIdAndEstado(torneo.getId(), "APROBADO");

        return TorneoResponse.builder()
                .id(torneo.getId())
                .nombre(torneo.getNombre())
                .descripcion(torneo.getDescripcion())
                .fechaInicio(torneo.getFechaInicio())
                .fechaFin(torneo.getFechaFin())
                .estado(torneo.getEstado())
                .nombreOrganizador(torneo.getOrganizador().getNombre())
                .cantidadEquipos((int) contadorReal)
                .build();
    }
}