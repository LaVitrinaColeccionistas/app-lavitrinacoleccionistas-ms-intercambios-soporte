package py.com.lavitrinacoleccionistas.intercambiossoporte.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import py.com.lavitrinacoleccionistas.entity.DetalleIntercambio;

import java.util.List;

@Repository
public interface IDetalleIntercambioRepository
        extends JpaRepository<DetalleIntercambio, Long> {

    List<DetalleIntercambio> findByIntercambioId(Long idIntercambio);
}