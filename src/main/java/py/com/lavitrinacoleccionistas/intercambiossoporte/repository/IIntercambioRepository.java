package py.com.lavitrinacoleccionistas.intercambiossoporte.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import py.com.lavitrinacoleccionistas.entity.Intercambio;
import py.com.lavitrinacoleccionistas.enums.EstadoIntercambio;

import java.util.Optional;

@Repository
public interface IIntercambioRepository
        extends IBaseRepository<Intercambio> {

    Optional<Intercambio> findByIdAndActivoTrue(Long id);

    @Query("""
            SELECT i
            FROM Intercambio i
            WHERE i.activo = true
              AND (
                    :idUsuario IS NULL
                    OR i.idUsuarioProponente = :idUsuario
                    OR i.idUsuarioReceptor = :idUsuario
                  )
              AND (
                    :estado IS NULL
                    OR i.estado = :estado
                  )
            """)
    Page<Intercambio> buscarActivos(
            @Param("idUsuario") Long idUsuario,
            @Param("estado") EstadoIntercambio estado,
            Pageable pageable
    );
}