package co.escorelab.scorelabbackend.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "partidos")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Partido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER) // Asegura que el torneo se cargue siempre
    @JoinColumn(name = "torneo_id", nullable = false)
    @JsonIgnoreProperties({"equipos", "partidos"}) // Evita recursión
    @JsonProperty("torneo")
    private Torneo torneo;

    @ManyToOne(fetch = FetchType.EAGER) // Carga los datos del equipo local
    @JoinColumn(name = "equipo_local_id", nullable = false)
    @JsonIgnoreProperties({"torneo", "jugadores"})
    @JsonProperty("equipoLocal")
    private Equipo equipoLocal;

    @ManyToOne(fetch = FetchType.EAGER) // Carga los datos del equipo visitante
    @JoinColumn(name = "equipo_visitante_id", nullable = false)
    @JsonIgnoreProperties({"torneo", "jugadores"})
    @JsonProperty("equipoVisitante")
    private Equipo equipoVisitante;

    @Column(name = "fecha_hora")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") // Formato compatible con JS Date
    @JsonProperty("fechaHora")
    private LocalDateTime fechaHora;

    private String lugar;

    @Column(name = "goles_local")
    @Builder.Default
    @JsonProperty("golesLocal")
    private Integer golesLocal = 0;

    @Column(name = "goles_visitante")
    @Builder.Default
    @JsonProperty("golesVisitante")
    private Integer golesVisitante = 0;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private EstadoPartido estado = EstadoPartido.PROGRAMADO;
}