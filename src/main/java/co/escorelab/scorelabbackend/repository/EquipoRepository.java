package co.escorelab.scorelabbackend.repository;

import co.escorelab.scorelabbackend.model.Equipo;
import co.escorelab.scorelabbackend.model.Torneo;
import co.escorelab.scorelabbackend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EquipoRepository extends JpaRepository<Equipo, Long> {

    // Para validar que no existan dos equipos con el mismo nombre
    Optional<Equipo> findByNombre(String nombre);

    // Para que un usuario delegado vea los equipos que ha creado
    List<Equipo> findByDelegado(Usuario delegado);

    // Para que el organizador encuentre todos los equipos que esperan aprobación
    List<Equipo> findByEstado(String estado);

    /**
     * 🌟 MÉTODO CLAVE PARA GESTIÓN DE PARTIDOS
     * Permite obtener la lista física de equipos vinculados a un torneo.
     * Este es el que utiliza el método listarEquiposPorTorneo en el Service.
     */
    List<Equipo> findByTorneo(Torneo torneo);

    /**
     * Alternativa si prefieres buscar directamente por el ID del torneo
     */
    List<Equipo> findByTorneoId(Long torneoId);

    /**
     * MÉTODO PARA EL CONTADOR
     * Cuenta todos los equipos asociados a un ID de torneo.
     */
    long countByTorneoId(Long torneoId);

    /**
     * Mantiene la compatibilidad para contar solo aprobados.
     */
    long countByTorneoIdAndEstado(Long torneoId, String estado);

    /**
     * Fuerza la vinculación del equipo con el torneo en la BD.
     */
    @Modifying
    @Transactional
    @Query("UPDATE Equipo e SET e.torneo = :torneo, e.estado = :estado WHERE e.id = :id")
    void vincularATorneoYAprobar(
            @Param("id") Long id,
            @Param("torneo") Torneo torneo,
            @Param("estado") String estado
    );
}