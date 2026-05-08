package co.escorelab.scorelabbackend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "torneos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Torneo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    private String descripcion;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;

    @ManyToOne
    @JoinColumn(name = "organizador_id", nullable = false)
    private Usuario organizador;

    @OneToMany(mappedBy = "torneo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Equipo> equipos;

    // 🌟 ESTA ES LA MAGIA DEL ESTADO EN TIEMPO REAL
    public EstadoTorneo getEstado() {
        LocalDate hoy = LocalDate.now(); // Toma la fecha de hoy

        if (hoy.isBefore(this.fechaInicio)) {
            return EstadoTorneo.PROXIMO;
        } else if (hoy.isAfter(this.fechaFin)) {
            return EstadoTorneo.FINALIZADO;
        } else {
            return EstadoTorneo.EN_CURSO;
        }
    }

    // Dentro de la clase Torneo.java
    public void agregarEquipo(Equipo equipo) {
        if (this.equipos == null) {
            this.equipos = new java.util.ArrayList<>();
        }
        this.equipos.add(equipo);
        equipo.setTorneo(this);
    }
}
