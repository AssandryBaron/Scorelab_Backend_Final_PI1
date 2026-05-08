package co.escorelab.scorelabbackend.repository;

import co.escorelab.scorelabbackend.model.Equipo;
import co.escorelab.scorelabbackend.model.Torneo;
import co.escorelab.scorelabbackend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface EquipoRepository extends JpaRepository<Equipo, Long> {

    // Para validar que no existan dos equipos con el mismo nombre
    Optional<Equipo> findByNombre(String nombre);

    // Para que un usuario delegado vea los equipos que ha creado
    List<Equipo> findByDelegado(Usuario delegado);

    // Para que el organizador encuentre todos los equipos que esperan aprobación
    List<Equipo> findByEstado(String estado);

    /**
     * 🌟 MÉTODO CLAVE: Fuerza la vinculación del equipo con el torneo en la BD.
     * Esto asegura que el campo torneo_id deje de ser NULL.
     */
    @Modifying
    @Transactional
    @Query("UPDATE Equipo e SET e.torneo = :torneo, e.estado = :estado WHERE e.id = :id")
    void vincularATorneoYAprobar(
            @Param("id") Long id,
            @Param("torneo") Torneo torneo,
            @Param("estado") String estado
    );

    // En EquipoRepository.java
    long countByTorneoIdAndEstado(Long torneoId, String estado);
}