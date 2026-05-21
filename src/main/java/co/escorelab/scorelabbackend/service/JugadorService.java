package co.escorelab.scorelabbackend.service;

import co.escorelab.scorelabbackend.dto.JugadorRequest;
import co.escorelab.scorelabbackend.model.Equipo;
import co.escorelab.scorelabbackend.model.Jugador;
import co.escorelab.scorelabbackend.repository.EquipoRepository;
import co.escorelab.scorelabbackend.repository.JugadorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JugadorService {

    private final JugadorRepository jugadorRepository;
    private final EquipoRepository equipoRepository;

    /**
     * ✅ NUEVO: Lista todos los jugadores de la base de datos.
     * Utilizado por el Organizador para la "Base de Jugadores".
     */
    public List<Jugador> listarTodosLosJugadores() {
        return jugadorRepository.findAll();
    }

    @Transactional
    public Jugador registrarJugador(Long equipoId, JugadorRequest request, String correoDelegado) {
        Equipo equipo = equipoRepository.findById(equipoId)
                .orElseThrow(() -> new RuntimeException("Equipo no encontrado"));

        // Validar propiedad del equipo
        validarAcceso(equipo, correoDelegado);

        // Validar reglas de negocio (DNI y Dorsal)
        validarReglasJugador(equipo, request);

        Jugador nuevoJugador = Jugador.builder()
                .nombre(request.getNombre())
                .documento(request.getDocumento())
                .posicion(request.getPosicion())
                .numeroCamiseta(request.getNumeroCamiseta())
                .equipo(equipo)
                .build();

        return jugadorRepository.save(nuevoJugador);
    }

    @Transactional
    public List<Jugador> registrarJugadoresLote(Long equipoId, List<JugadorRequest> requests, String correoDelegado) {
        Equipo equipo = equipoRepository.findById(equipoId)
                .orElseThrow(() -> new RuntimeException("Equipo no encontrado"));

        validarAcceso(equipo, correoDelegado);

        // Procesar cada jugador del lote
        return requests.stream()
                .map(request -> {
                    validarReglasJugador(equipo, request);
                    return Jugador.builder()
                            .nombre(request.getNombre())
                            .documento(request.getDocumento())
                            .posicion(request.getPosicion())
                            .numeroCamiseta(request.getNumeroCamiseta())
                            .equipo(equipo)
                            .build();
                })
                .map(jugadorRepository::save)
                .collect(Collectors.toList());
    }

    public List<Jugador> listarJugadoresPorEquipo(Long equipoId) {
        if (!equipoRepository.existsById(equipoId)) {
            throw new RuntimeException("El equipo solicitado no existe");
        }
        return jugadorRepository.findByEquipoId(equipoId);
    }

    // --- MÉTODOS DE APOYO ---

    private void validarAcceso(Equipo equipo, String correoDelegado) {
        if (!equipo.getDelegado().getCorreo().equals(correoDelegado)) {
            throw new RuntimeException("No tienes permisos para gestionar este equipo");
        }
    }

    private void validarReglasJugador(Equipo equipo, JugadorRequest request) {
        // 1. Validar Documento Duplicado (Global)
        if (jugadorRepository.findByDocumento(request.getDocumento()).isPresent()) {
            throw new RuntimeException("⚠ La cédula '" + request.getDocumento() + "' ya pertenece a un jugador registrado.");
        }

        // 2. Validar Dorsal Duplicado (Solo en este equipo)
        if (request.getNumeroCamiseta() != null &&
                jugadorRepository.existsByEquipoAndNumeroCamiseta(equipo, request.getNumeroCamiseta())) {
            throw new RuntimeException("⚠ El dorsal #" + request.getNumeroCamiseta() + " ya está ocupado en el equipo " + equipo.getNombre());
        }
    }
}