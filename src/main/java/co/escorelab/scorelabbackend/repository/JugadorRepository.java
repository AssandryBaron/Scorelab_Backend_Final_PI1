package co.escorelab.scorelabbackend.repository;

import co.escorelab.scorelabbackend.model.Equipo;
import co.escorelab.scorelabbackend.model.Jugador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JugadorRepository extends JpaRepository<Jugador, Long> {

    // Para validar que la cédula sea única en todo el sistema
    Optional<Jugador> findByDocumento(String documento);

    // Para cargar la "Plantilla Actual" en el frontend
    List<Jugador> findByEquipoId(Long equipoId);

    // Para validar que no se repita el número de camiseta en el mismo equipo
    boolean existsByEquipoAndNumeroCamiseta(Equipo equipo, Integer numeroCamiseta);
}