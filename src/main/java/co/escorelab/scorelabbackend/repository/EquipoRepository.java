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
     * Permite obtener la lista física de equipos vinculados a un torneo (Sin filtrar).
     */
    List<Equipo> findByTorneo(Torneo torneo);

    /**
     * Alternativa si prefieres buscar directamente por el ID del torneo
     */
    List<Equipo> findByTorneoId(Long torneoId);

    /**
     * 🎯 NUEVO MÉTODO PARA GESTIÓN DE PARTIDOS
     * Filtra directamente desde la base de datos trayendo únicamente los equipos de un
     * torneo que tengan un estado específico (Ej: "APROBADO"). Ignora mayúsculas/minúsculas.
     */
    List<Equipo> findByTorneoAndEstadoIgnoreCase(Torneo torneo, String estado);

    /**
     * 📊 CONTADOR CORREGIDO PARA EL TORNEO
     * Cuenta únicamente los equipos de un torneo que estén en un estado específico.
     * Al usar este en tus contadores pasando "APROBADO", los rechazados dejarán de sumar automáticamente.
     */
    long countByTorneoIdAndEstadoIgnoreCase(Long torneoId, String estado);

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