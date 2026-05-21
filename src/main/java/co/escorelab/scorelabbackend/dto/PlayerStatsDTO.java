package co.escorelab.scorelabbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de salida que representa las estadísticas individuales de un jugador
 * en un torneo: goles anotados, tarjetas amarillas y tarjetas rojas.
 * Calculado en StatService a partir de los EventoPartido registrados.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlayerStatsDTO {

    /** ID del jugador */
    private Long jugadorId;

    /** Nombre completo del jugador */
    private String nombre;

    /** Nombre del equipo al que pertenece */
    private String equipo;

    /** Total de goles anotados en partidos FINALIZADOS del torneo */
    private long goles;

    /** Total de tarjetas amarillas recibidas */
    private long amarillas;

    /** Total de tarjetas rojas recibidas (directas + doble amarilla) */
    private long rojas;
}
