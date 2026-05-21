package co.escorelab.scorelabbackend.repository;

import co.escorelab.scorelabbackend.model.EventoPartido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EventoPartidoRepository extends JpaRepository<EventoPartido, Long> {

    /**
     * Obtiene los eventos de un partido ordenados del minuto 1 al 90.
     * Útil para reportes finales o actas de partido.
     */
    List<EventoPartido> findByPartidoIdOrderByMinutoAsc(Long partidoId);

    /**
     * ✅ RECOMENDADO PARA EL CONTROL EN VIVO:
     * Obtiene los eventos ordenados del más reciente al más antiguo.
     * Esto hará que cuando registres un gol, aparezca de primero en la lista visual.
     */
    List<EventoPartido> findByPartidoIdOrderByMinutoDesc(Long partidoId);
}