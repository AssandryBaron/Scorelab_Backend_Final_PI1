package co.escorelab.scorelabbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de salida que representa una fila en la Tabla de Posiciones.
 * Todos los campos son calculados en StandingService a partir
 * de los partidos FINALIZADOS de un torneo específico.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StandingDTO {

    /** ID del equipo (para el frontend, no lo muestra pero lo usa como key) */
    private Long equipoId;

    /** Nombre del equipo para mostrar en la tabla */
    private String nombre;

    /** Partidos Jugados */
    private int pj;

    /** Partidos Ganados (3 pts) */
    private int pg;

    /** Partidos Empatados (1 pt) */
    private int pe;

    /** Partidos Perdidos (0 pts) */
    private int pp;

    /** Goles a Favor */
    private int gf;

    /** Goles en Contra */
    private int gc;

    /** Diferencia de Goles: gf - gc */
    private int dif;

    /** Puntos totales: (pg * 3) + (pe * 1) */
    private int pts;
}
