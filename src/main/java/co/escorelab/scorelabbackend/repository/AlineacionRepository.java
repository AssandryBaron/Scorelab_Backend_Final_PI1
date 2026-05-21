package co.escorelab.scorelabbackend.repository;

import co.escorelab.scorelabbackend.model.Alineacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AlineacionRepository extends JpaRepository<Alineacion, Long> {

    // Busca los jugadores titulares registrados para un partido específico
    List<Alineacion> findByPartidoId(Long partidoId);

    // Opcional: Verifica si ya existe alineación para no duplicarla
    boolean existsByPartidoId(Long partidoId);
}