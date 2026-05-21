package co.escorelab.scorelabbackend.service;

import co.escorelab.scorelabbackend.dto.PartidoRequest;
import co.escorelab.scorelabbackend.model.*;
import co.escorelab.scorelabbackend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PartidoService {

    private final PartidoRepository partidoRepository;
    private final EquipoRepository equipoRepository;
    private final TorneoRepository torneoRepository;
    private final JugadorRepository jugadorRepository;
    private final EventoPartidoRepository eventoPartidoRepository;
    private final AlineacionRepository alineacionRepository;

    @Transactional(readOnly = true)
    public Partido buscarPorId(Long id) {
        return partidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Partido no encontrado con ID: " + id));
    }

    // --- GESTIÓN DE TORNEO ---

    @Transactional(readOnly = true)
    public List<Partido> listarTodos() {
        return partidoRepository.findAll();
    }

    /**
     * ✅ CORRECCIÓN CRÍTICA:
     * Cambiamos el nombre a 'obtenerPartidosPorTorneo' para que coincida
     * exactamente con lo que el PartidoController está buscando.
     */
    @Transactional(readOnly = true)
    public List<Partido> listarPorTorneo(Long torneoId) {
        return partidoRepository.findByTorneoId(torneoId);
    }

    // --- CONTROL DE PARTIDO (PLANTILLA Y EVENTOS) ---

    @Transactional
    public void guardarPlantilla(Long partidoId, List<Long> jugadorIds) {
        Partido partido = buscarPorId(partidoId);

        // Limpiar alineación previa para evitar duplicados al confirmar
        List<Alineacion> previa = alineacionRepository.findByPartidoId(partidoId);
        if (!previa.isEmpty()) {
            alineacionRepository.deleteAll(previa);
        }

        for (Long jId : jugadorIds) {
            Jugador jugador = jugadorRepository.findById(jId)
                    .orElseThrow(() -> new RuntimeException("Jugador no encontrado: " + jId));

            Alineacion alineacion = Alineacion.builder()
                    .partido(partido)
                    .jugador(jugador)
                    .esTitular(true)
                    .build();
            alineacionRepository.save(alineacion);
        }
    }

    @Transactional(readOnly = true)
    public List<EventoPartido> obtenerCronologia(Long partidoId) {
        return eventoPartidoRepository.findByPartidoIdOrderByMinutoDesc(partidoId);
    }

    @Transactional
    public EventoPartido registrarEvento(Long partidoId, Long jugadorId, Integer minuto, TipoEvento tipo) {
        Partido partido = buscarPorId(partidoId);
        Jugador jugador = jugadorRepository.findById(jugadorId)
                .orElseThrow(() -> new RuntimeException("Jugador no encontrado"));

        if (tipo == TipoEvento.GOL) {
            actualizarMarcadorPorGol(partido, jugador);
        }

        EventoPartido evento = EventoPartido.builder()
                .partido(partido)
                .jugador(jugador)
                .minuto(minuto)
                .tipo(tipo)
                .build();

        return eventoPartidoRepository.save(evento);
    }

    private void actualizarMarcadorPorGol(Partido partido, Jugador anotador) {
        if (Objects.equals(anotador.getEquipo().getId(), partido.getEquipoLocal().getId())) {
            partido.setGolesLocal(partido.getGolesLocal() + 1);
        } else if (Objects.equals(anotador.getEquipo().getId(), partido.getEquipoVisitante().getId())) {
            partido.setGolesVisitante(partido.getGolesVisitante() + 1);
        }
        partidoRepository.save(partido);
    }

    // --- PROGRAMACIÓN Y RESULTADOS ---

    @Transactional
    public Partido programarPartido(PartidoRequest req) {
        Torneo torneo = torneoRepository.findById(req.getTorneoId())
                .orElseThrow(() -> new RuntimeException("Torneo no encontrado"));
        Equipo local = equipoRepository.findById(req.getLocalId())
                .orElseThrow(() -> new RuntimeException("Equipo local no encontrado"));
        Equipo visitante = equipoRepository.findById(req.getVisitanteId())
                .orElseThrow(() -> new RuntimeException("Equipo visitante no encontrado"));

        Partido partido = Partido.builder()
                .torneo(torneo)
                .equipoLocal(local)
                .equipoVisitante(visitante)
                .fechaHora(req.getFechaHora())
                .lugar(req.getLugar())
                .estado(EstadoPartido.PROGRAMADO)
                .golesLocal(0)
                .golesVisitante(0)
                .build();

        return partidoRepository.save(partido);
    }

    @Transactional
    public Partido registrarResultado(Long partidoId, Integer golesLocal, Integer golesVisitante) {
        Partido partido = buscarPorId(partidoId);
        partido.setGolesLocal(golesLocal);
        partido.setGolesVisitante(golesVisitante);
        partido.setEstado(EstadoPartido.FINALIZADO);
        return partidoRepository.save(partido);
    }

    @Transactional
    public void eliminarPartido(Long partidoId) {
        partidoRepository.deleteById(partidoId);
    }
}