package py.com.lavitrinacoleccionistas.intercambiossoporte.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import py.com.lavitrinacoleccionistas.entity.DetalleIntercambio;

import java.util.List;

@Repository
public interface IDetalleIntercambioRepository
        extends IBaseRepository<DetalleIntercambio> {

    @Query("""
        SELECT d
        FROM DetalleIntercambio d
        WHERE d.intercambio.id = :idIntercambio
        """)
    List<DetalleIntercambio> buscarPorIntercambio(
            @Param("idIntercambio") Long idIntercambio
    );
}