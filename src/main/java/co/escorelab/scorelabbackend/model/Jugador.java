package co.escorelab.scorelabbackend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "jugadores",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_equipo_dorsal",
                        columnNames = {"equipo_id", "numero_camiseta"}
                )
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Jugador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false, unique = true)
    private String documento; // Cédula (única en todo el sistema)

    private String posicion;

    @Column(name = "numero_camiseta")
    private Integer numeroCamiseta;

    @ManyToOne
    @JoinColumn(name = "equipo_id", nullable = false)
    private Equipo equipo;
}
