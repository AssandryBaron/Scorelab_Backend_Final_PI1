package co.escorelab.scorelabbackend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "alineaciones")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Alineacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "partido_id", nullable = false)
    private Partido partido;

    @ManyToOne
    @JoinColumn(name = "jugador_id", nullable = false)
    private Jugador jugador;

    // Esto nos dirá si es titular (los 11) o suplente
    @Builder.Default
    private boolean esTitular = true;
}