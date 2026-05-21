package co.escorelab.scorelabbackend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "eventos_partido")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventoPartido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partido_id", nullable = false)
    private Partido partido;

    /**
     * Jugador principal del evento:
     * - Si es GOL: El anotador.
     * - Si es TARJETA: El amonestado.
     * - Si es CAMBIO: El jugador que SALE del campo.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jugador_id") // Quitamos nullable=false por si hay eventos globales
    private Jugador jugador;

    /**
     * NUEVO: Jugador que entra al campo.
     * Solo se utiliza cuando el tipo de evento es 'CAMBIO'.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jugador_entrada_id")
    private Jugador jugadorEntrada;

    @Column(nullable = false)
    private Integer minuto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoEvento tipo;

    // Opcional: Descripción extra (ej. "Entra por lesión")
    private String observacion;
}