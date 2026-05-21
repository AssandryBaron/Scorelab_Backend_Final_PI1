package co.escorelab.scorelabbackend.service;

import co.escorelab.scorelabbackend.dto.StandingDTO;
import co.escorelab.scorelabbackend.model.EstadoPartido;
import co.escorelab.scorelabbackend.model.Partido;
import co.escorelab.scorelabbackend.repository.PartidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * StandingService — Capa de negocio para la Tabla de Posiciones.
 *
 * <p>LÓGICA DE PUNTUACIÓN ESTÁNDAR:
 * <ul>
 *   <li>Victoria  → 3 puntos al ganador, 0 al perdedor</li>
 *   <li>Empate    → 1 punto a cada equipo</li>
 *   <li>Derrota   → 0 puntos</li>
 * </ul>
 *
 * <p>CRITERIOS DE DESEMPATE (orden de prioridad):
 * <ol>
 *   <li>Mayor cantidad de puntos (pts DESC)</li>
 *   <li>Mayor diferencia de goles (dif DESC)</li>
 *   <li>Mayor cantidad de goles a favor (gf DESC)</li>
 *   <li>Orden alfabético del nombre del equipo</li>
 * </ol>
 *
 * <p>Solo se procesan partidos en estado {@code FINALIZADO}.
 */
@Service
@RequiredArgsConstructor
public class StandingService {

    private final PartidoRepository partidoRepository;

    /**
     * Calcula y retorna la tabla de posiciones ordenada para un torneo dado.
     *
     * @param torneoId ID del torneo a consultar
     * @return Lista de {@link StandingDTO} ordenada por posición en la tabla
     */
    public List<StandingDTO> calcularPosiciones(Long torneoId) {
        // 1. Obtener SOLO los partidos FINALIZADOS del torneo
        List<Partido> partidos = partidoRepository.findByTorneoId(torneoId)
                .stream()
                .filter(p -> EstadoPartido.FINALIZADO.equals(p.getEstado()))
                .toList();

        // 2. Acumular estadísticas por equipo usando un Map <equipoId, acumulador>
        Map<Long, StandingAccumulator> tabla = new LinkedHashMap<>();

        for (Partido partido : partidos) {
            long localId    = partido.getEquipoLocal().getId();
            long visitanteId = partido.getEquipoVisitante().getId();

            // Inicializar acumuladores si es la primera vez que aparece el equipo
            tabla.computeIfAbsent(localId,     id -> new StandingAccumulator(partido.getEquipoLocal().getNombre()));
            tabla.computeIfAbsent(visitanteId, id -> new StandingAccumulator(partido.getEquipoVisitante().getNombre()));

            int golesLocal      = partido.getGolesLocal()      != null ? partido.getGolesLocal()      : 0;
            int golesVisitante  = partido.getGolesVisitante()  != null ? partido.getGolesVisitante()  : 0;

            StandingAccumulator local     = tabla.get(localId);
            StandingAccumulator visitante = tabla.get(visitanteId);

            // Acumular goles
            local.gf     += golesLocal;
            local.gc     += golesVisitante;
            visitante.gf += golesVisitante;
            visitante.gc += golesLocal;

            // Incrementar partidos jugados
            local.pj++;
            visitante.pj++;

            // Determinar resultado y asignar puntos
            if (golesLocal > golesVisitante) {
                // Victoria del LOCAL
                local.pg++;
                local.pts += 3;
                visitante.pp++;
            } else if (golesVisitante > golesLocal) {
                // Victoria del VISITANTE
                visitante.pg++;
                visitante.pts += 3;
                local.pp++;
            } else {
                // EMPATE
                local.pe++;
                local.pts++;
                visitante.pe++;
                visitante.pts++;
            }
        }

        // 3. Convertir acumuladores a DTOs y aplicar criterios de desempate
        return tabla.entrySet().stream()
                .map(entry -> {
                    StandingAccumulator acc = entry.getValue();
                    return StandingDTO.builder()
                            .equipoId(entry.getKey())
                            .nombre(acc.nombre)
                            .pj(acc.pj)
                            .pg(acc.pg)
                            .pe(acc.pe)
                            .pp(acc.pp)
                            .gf(acc.gf)
                            .gc(acc.gc)
                            .dif(acc.gf - acc.gc)
                            .pts(acc.pts)
                            .build();
                })
                .sorted(Comparator
                        .comparingInt(StandingDTO::getPts).reversed()
                        .thenComparingInt(StandingDTO::getDif).reversed()
                        .thenComparingInt(StandingDTO::getGf).reversed()
                        .thenComparing(StandingDTO::getNombre))
                .toList();
    }

    // ─── Clase auxiliar interna para acumular estadísticas ───────────────────

    private static class StandingAccumulator {
        final String nombre;
        int pj = 0, pg = 0, pe = 0, pp = 0;
        int gf = 0, gc = 0, pts = 0;

        StandingAccumulator(String nombre) {
            this.nombre = nombre;
        }
    }
}
