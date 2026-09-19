package py.com.lavitrinacoleccionistas.intercambiossoporte.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import py.com.lavitrinacoleccionistas.entity.Intercambio;

import java.util.Optional;

@Repository
public interface IIntercambioRepository
        extends JpaRepository<Intercambio, Long>,
        JpaSpecificationExecutor<Intercambio> {

    Optional<Intercambio> findByIdAndActivoTrue(Long id);
}