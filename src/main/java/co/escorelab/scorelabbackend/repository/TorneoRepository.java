package co.escorelab.scorelabbackend.repository;

import co.escorelab.scorelabbackend.model.Torneo;
import co.escorelab.scorelabbackend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TorneoRepository extends JpaRepository<Torneo, Long> {
    // Para que un organizador vea solo los torneos que él creó
    List<Torneo> findByOrganizador(Usuario organizador);
}
