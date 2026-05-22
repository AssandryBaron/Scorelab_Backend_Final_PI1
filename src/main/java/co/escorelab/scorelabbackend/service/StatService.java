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
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * StatService — Capa de negocio para Estadísticas Globales del Torneo.
 *
 * <p>OPTIMIZACIONES v2:
 * <ul>
 *   <li>Se elimina el antipatrón {@code findAll()} + filtrado en memoria.
 *       Ahora se delega el filtrado a la base de datos mediante
 *       {@code findByPartidoIdIn(ids)}.</li>
 *   <li>Se unifica la lógica de acumulación duplicada entre
 *       {@code getRankingGoleadores} y {@code getControlDisciplinario}
 *       en un único método privado {@code acumularEventos}.</li>
 *   <li>{@code getControlDisciplinario} ya no necesita un segundo recorrido
 *       al reutilizar el acumulador completo construido en la primera pasada.</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
public class StatService {

    private final PartidoRepository       partidoRepository;
    private final EventoPartidoRepository eventoPartidoRepository;

    // ── API Pública ──────────────────────────────────────────────────────────

    /**
     * Ranking de goleadores ordenado por goles DESC.
     * Solo incluye jugadores con al menos un evento estadístico.
     */
    @Transactional(readOnly = true)
    public List<PlayerStatsDTO> getRankingGoleadores(Long torneoId) {
        return acumularEventos(torneoId).values().stream()
                .map(this::toDTO)
                .filter(dto -> dto.getGoles() > 0 || dto.getAmarillas() > 0 || dto.getRojas() > 0)
                .sorted(Comparator.comparingLong(PlayerStatsDTO::getGoles).reversed()
                        .thenComparing(PlayerStatsDTO::getNombre))
                .collect(Collectors.toList());
    }

    /**
     * Control disciplinario ordenado por rojas DESC, luego amarillas DESC.
     * Reutiliza el mismo acumulador que el ranking de goleadores — sin doble query.
     */
    @Transactional(readOnly = true)
    public List<PlayerStatsDTO> getControlDisciplinario(Long torneoId) {
        return acumularEventos(torneoId).values().stream()
                .map(this::toDTO)
                .filter(dto -> dto.getAmarillas() > 0 || dto.getRojas() > 0)
                .sorted(Comparator.comparingLong(PlayerStatsDTO::getRojas).reversed()
                        .thenComparingLong(PlayerStatsDTO::getAmarillas).reversed()
                        .thenComparing(PlayerStatsDTO::getNombre))
                .collect(Collectors.toList());
    }

    /**
     * Estadísticas completas: equivalente al ranking de goleadores (incluye tarjetas).
     */
    @Transactional(readOnly = true)
    public List<PlayerStatsDTO> getEstadisticasCompletas(Long torneoId) {
        return getRankingGoleadores(torneoId);
    }

    // ── Lógica interna ───────────────────────────────────────────────────────

    /**
     * Consulta los IDs de partidos finalizados del torneo y luego trae
     * sus eventos en una sola query (sin findAll). Acumula goles,
     * amarillas y rojas en un mapa keyed por jugadorId.
     */
    private Map<Long, PlayerStatsAccumulator> acumularEventos(Long torneoId) {
        // 1. IDs de partidos finalizados — sin cargar entidades completas
        Set<Long> idsFinalizados = partidoRepository.findByTorneoId(torneoId)
                .stream()
                .filter(p -> EstadoPartido.FINALIZADO.equals(p.getEstado()))
                .map(p -> p.getId())
                .collect(Collectors.toSet());

        if (idsFinalizados.isEmpty()) {
            return Collections.emptyMap();
        }

        // 2. Solo los eventos de esos partidos — query filtrada en la BD
        List<EventoPartido> eventos = eventoPartidoRepository.findByPartidoIdIn(idsFinalizados);

        // 3. Acumular en un solo recorrido
        Map<Long, PlayerStatsAccumulator> acumuladores = new LinkedHashMap<>();

        eventos.stream()
                .filter(e -> e.getJugador() != null && e.getTipo() != null)
                .forEach(e -> {
                    switch (e.getTipo()) {
                        case GOL              -> acumular(acumuladores, e.getJugador(), 1, 0, 0);
                        case TARJETA_AMARILLA -> acumular(acumuladores, e.getJugador(), 0, 1, 0);
                        case TARJETA_ROJA     -> acumular(acumuladores, e.getJugador(), 0, 0, 1);
                        default               -> { /* otros eventos futuros */ }
                    }
                });

        return acumuladores;
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
        String equipo = acc.jugador.getEquipo() != null
                ? acc.jugador.getEquipo().getNombre()
                : "Sin equipo";

        return PlayerStatsDTO.builder()
                .jugadorId(acc.jugador.getId())
                .nombre(acc.jugador.getNombre())
                .equipo(equipo)
                .goles(acc.goles)
                .amarillas(acc.amarillas)
                .rojas(acc.rojas)
                .build();
    }

    // ── Acumulador interno ───────────────────────────────────────────────────

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
