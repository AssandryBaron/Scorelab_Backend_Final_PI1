package co.escorelab.scorelabbackend.repository;

import co.escorelab.scorelabbackend.model.EventoPartido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface EventoPartidoRepository extends JpaRepository<EventoPartido, Long> {

    /** Cronología ascendente (acta de partido). */
    List<EventoPartido> findByPartidoIdOrderByMinutoAsc(Long partidoId);

    /** Cronología descendente (control en vivo — el gol más reciente aparece primero). */
    List<EventoPartido> findByPartidoIdOrderByMinutoDesc(Long partidoId);

    /**
     * ✅ OPTIMIZACIÓN: Reemplaza el findAll() + filtrado en memoria del StatService.
     * Trae SOLO los eventos de los partidos finalizados de un torneo en una sola query,
     * evitando cargar toda la tabla de eventos en memoria.
     */
    @Query("SELECT e FROM EventoPartido e WHERE e.partido.id IN :partidoIds")
    List<EventoPartido> findByPartidoIdIn(@Param("partidoIds") Set<Long> partidoIds);
}
