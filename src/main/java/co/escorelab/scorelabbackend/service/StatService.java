package co.escorelab.scorelabbackend.service;

import co.escorelab.scorelabbackend.dto.PlayerStatsDTO;
import co.escorelab.scorelabbackend.model.EstadoPartido;
import co.escorelab.scorelabbackend.model.EventoPartido;
import co.escorelab.scorelabbackend.model.Jugador;
import co.escorelab.scorelabbackend.model.TipoEvento;
import co.escorelab.scorelabbackend.repository.EventoPartidoRepository;
import co.escorelab.scorelabbackend.repository.PartidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * StatService — Capa de negocio para Estadísticas Globales del Torneo.
 */
@Service
@RequiredArgsConstructor
public class StatService {

    private final PartidoRepository         partidoRepository;
    private final EventoPartidoRepository   eventoPartidoRepository;

    // ── Ranking de Goleadores (CORREGIDO PARA TRAER TARJETAS SIMULTÁNEAMENTE) ──

    /**
     * Retorna las estadísticas de rendimiento de los jugadores en un torneo,
     * acumulando goles y tarjetas al mismo tiempo para alimentar la vista dual del front.
     *
     * @param torneoId ID del torneo a consultar
     * @return Lista de {@link PlayerStatsDTO} con métricas completas
     */
    public List<PlayerStatsDTO> getRankingGoleadores(Long torneoId) {
        List<EventoPartido> eventos = getEventosFinalizados(torneoId);

        Map<Long, PlayerStatsAccumulator> acumuladores = new LinkedHashMap<>();

        // ✨ CORRECCIÓN CRÍTICA: Mapeamos goles, amarillas y rojas en el mismo mapa
        eventos.stream()
                .filter(e -> e.getJugador() != null)
                .forEach(e -> {
                    if (e.getTipo() != null) {
                        switch (e.getTipo()) {
                            case GOL              -> acumular(acumuladores, e.getJugador(), 1, 0, 0);
                            case TARJETA_AMARILLA -> acumular(acumuladores, e.getJugador(), 0, 1, 0);
                            case TARJETA_ROJA     -> acumular(acumuladores, e.getJugador(), 0, 0, 1);
                            default               -> { /* Ignorar otros eventos */ }
                        }
                    }
                });

        return acumuladores.values().stream()
                .map(this::toDTO)
                // Filtramos para que se incluyan jugadores que tengan participación estadística real
                .filter(dto -> dto.getGoles() > 0 || dto.getAmarillas() > 0 || dto.getRojas() > 0)
                .sorted(Comparator.comparingLong(PlayerStatsDTO::getGoles).reversed()
                        .thenComparing(PlayerStatsDTO::getNombre))
                .collect(Collectors.toList());
    }

    // ── Control Disciplinario ────────────────────────────────────────────────

    public List<PlayerStatsDTO> getControlDisciplinario(Long torneoId) {
        List<EventoPartido> eventos = getEventosFinalizados(torneoId);

        Map<Long, PlayerStatsAccumulator> acumuladores = new LinkedHashMap<>();

        eventos.stream()
                .filter(e -> e.getJugador() != null)
                .forEach(e -> {
                    if (TipoEvento.TARJETA_AMARILLA.equals(e.getTipo())) {
                        acumular(acumuladores, e.getJugador(), 0, 1, 0);
                    } else if (TipoEvento.TARJETA_ROJA.equals(e.getTipo())) {
                        acumular(acumuladores, e.getJugador(), 0, 0, 1);
                    }
                });

        return acumuladores.values().stream()
                .map(this::toDTO)
                .filter(dto -> dto.getAmarillas() > 0 || dto.getRojas() > 0)
                .sorted(Comparator.comparingLong(PlayerStatsDTO::getRojas).reversed()
                        .thenComparingLong(PlayerStatsDTO::getAmarillas).reversed()
                        .thenComparing(PlayerStatsDTO::getNombre))
                .collect(Collectors.toList());
    }

    // ── Estadísticas Completas ───────────────────────────────────────────────

    public List<PlayerStatsDTO> getEstadisticasCompletas(Long torneoId) {
        return getRankingGoleadores(torneoId);
    }

    // ── Métodos privados de apoyo ────────────────────────────────────────────

    private List<EventoPartido> getEventosFinalizados(Long torneoId) {
        Set<Long> idsFinalizados = partidoRepository.findByTorneoId(torneoId)
                .stream()
                .filter(p -> EstadoPartido.FINALIZADO.equals(p.getEstado()))
                .map(p -> p.getId())
                .collect(Collectors.toSet());

        if (idsFinalizados.isEmpty()) {
            return Collections.emptyList();
        }

        return eventoPartidoRepository.findAll()
                .stream()
                .filter(e -> e.getPartido() != null
                        && idsFinalizados.contains(e.getPartido().getId()))
                .collect(Collectors.toList());
    }

    private void acumular(Map<Long, PlayerStatsAccumulator> mapa,
                          Jugador jugador, int goles, int amarillas, int rojas) {
        mapa.computeIfAbsent(jugador.getId(), id -> new PlayerStatsAccumulator(jugador));
        PlayerStatsAccumulator acc = mapa.get(jugador.getId());
        acc.goles     += goles;
        acc.amarillas += amarillas;
        acc.rojas     += rojas;
    }

    private PlayerStatsDTO toDTO(PlayerStatsAccumulator acc) {
        String nombreEquipo = acc.jugador.getEquipo() != null
                ? acc.jugador.getEquipo().getNombre()
                : "Sin equipo";

        return PlayerStatsDTO.builder()
                .jugadorId(acc.jugador.getId())
                .nombre(acc.jugador.getNombre())
                .equipo(nombreEquipo)
                .goles(acc.goles)
                .amarillas(acc.amarillas)
                .rojas(acc.rojas)
                .build();
    }

    private static class PlayerStatsAccumulator {
        final Jugador jugador;
        long goles     = 0;
        long amarillas = 0;
        long rojas     = 0;

        PlayerStatsAccumulator(Jugador jugador) {
            this.jugador = jugador;
        }
    }
}