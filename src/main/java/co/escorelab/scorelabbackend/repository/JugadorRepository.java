package co.escorelab.scorelabbackend.repository;

import co.escorelab.scorelabbackend.model.Jugador;
import co.escorelab.scorelabbackend.model.Equipo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface JugadorRepository extends JpaRepository<Jugador, Long> {
    Optional<Jugador> findByDocumento(String documento);
    List<Jugador> findByEquipo(Equipo equipo);

    // Este nos servirá para la validación limpia en el servicio
    boolean existsByEquipoAndNumeroCamiseta(Equipo equipo, Integer numeroCamiseta);
}
