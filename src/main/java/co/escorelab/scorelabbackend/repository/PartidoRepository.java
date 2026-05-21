package co.escorelab.scorelabbackend.repository;

import co.escorelab.scorelabbackend.model.Partido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PartidoRepository extends JpaRepository<Partido, Long> {

    // Este es el que usa el servicio para la gestión de torneos
    List<Partido> findByTorneoId(Long torneoId);

    // Este es útil para el calendario cronológico
    List<Partido> findByTorneoIdOrderByFechaHoraAsc(Long torneoId);
}