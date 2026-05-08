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
     * Registra un solo jugador validando permisos y unicidad.
     */
    @Transactional
    public Jugador registrarJugador(Long equipoId, JugadorRequest request, String correoDelegado) {
        Equipo equipo = equipoRepository.findById(equipoId)
                .orElseThrow(() -> new RuntimeException("Equipo no encontrado"));

        // 1. Validar que el usuario logueado sea el dueño del equipo
        if (!equipo.getDelegado().getCorreo().equals(correoDelegado)) {
            throw new RuntimeException("No tienes permisos para gestionar este equipo");
        }

        // 2. Validar documento único global
        if (jugadorRepository.findByDocumento(request.getDocumento()).isPresent()) {
            throw new RuntimeException("Ya existe un jugador con el documento: " + request.getDocumento());
        }

        // 3. Validar dorsal único en el equipo
        if (request.getNumeroCamiseta() != null &&
                jugadorRepository.existsByEquipoAndNumeroCamiseta(equipo, request.getNumeroCamiseta())) {
            throw new RuntimeException("El número " + request.getNumeroCamiseta() + " ya está ocupado en este equipo");
        }

        Jugador nuevoJugador = Jugador.builder()
                .nombre(request.getNombre())
                .documento(request.getDocumento())
                .posicion(request.getPosicion())
                .numeroCamiseta(request.getNumeroCamiseta())
                .equipo(equipo)
                .build();

        return jugadorRepository.save(nuevoJugador);
    }

    /**
     * 🌟 NUEVO: Registra una lista de jugadores en lote.
     * Si uno falla, todos se revierten (Rollback).
     */
    @Transactional
    public List<Jugador> registrarJugadoresLote(Long equipoId, List<JugadorRequest> requests, String correoDelegado) {
        // Validamos el equipo una sola vez al inicio para ahorrar recursos
        Equipo equipo = equipoRepository.findById(equipoId)
                .orElseThrow(() -> new RuntimeException("Equipo no encontrado"));

        if (!equipo.getDelegado().getCorreo().equals(correoDelegado)) {
            throw new RuntimeException("No tienes permisos para gestionar este equipo");
        }

        // Procesamos la lista usando el método individual para reutilizar las validaciones
        return requests.stream()
                .map(request -> registrarJugador(equipoId, request, correoDelegado))
                .collect(Collectors.toList());
    }

    /**
     * Lista los jugadores pertenecientes a un equipo específico.
     */
    public List<Jugador> listarJugadoresPorEquipo(Long equipoId) {
        Equipo equipo = equipoRepository.findById(equipoId)
                .orElseThrow(() -> new RuntimeException("Equipo no encontrado"));
        return jugadorRepository.findByEquipo(equipo);
    }
}