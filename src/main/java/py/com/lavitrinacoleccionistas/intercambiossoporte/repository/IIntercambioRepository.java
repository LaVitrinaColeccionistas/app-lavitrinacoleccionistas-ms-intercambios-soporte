package py.com.lavitrinacoleccionistas.intercambiossoporte.repository;

import org.springframework.stereotype.Repository;
import py.com.lavitrinacoleccionistas.entity.Intercambio;

import java.util.Optional;

@Repository
public interface IIntercambioRepository
        extends IBaseRepository<Intercambio> {

    Optional<Intercambio> findByIdAndActivoTrue(Long id);
}
