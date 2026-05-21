package co.escorelab.scorelabbackend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
    private String documento; // Cédula (única)

    private String posicion;

    @Column(name = "numero_camiseta")
    private Integer numeroCamiseta;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "equipo_id", nullable = false)
    @ToString.Exclude // Evita errores de recursión al hacer log o debug
    @JsonBackReference // Evita bucles infinitos al convertir a JSON
    private Equipo equipo;
}