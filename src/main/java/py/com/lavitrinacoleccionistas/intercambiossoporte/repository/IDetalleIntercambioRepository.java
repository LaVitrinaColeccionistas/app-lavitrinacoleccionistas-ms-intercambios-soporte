package py.com.lavitrinacoleccionistas.intercambiossoporte.repository;

import org.springframework.stereotype.Repository;
import py.com.lavitrinacoleccionistas.entity.DetalleIntercambio;

import java.util.List;

@Repository
public interface IDetalleIntercambioRepository
        extends IBaseRepository<DetalleIntercambio> {

    List<DetalleIntercambio> findByIntercambioId(Long idIntercambio);
}