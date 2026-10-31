package py.com.lavitrinacoleccionistas.intercambiossoporte.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import py.com.lavitrinacoleccionistas.entity.Intercambio;
import py.com.lavitrinacoleccionistas.enums.EstadoIntercambio;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface IIntercambioRepository
        extends IBaseRepository<Intercambio> {

    @Query("""
        SELECT i
        FROM Intercambio i
        WHERE i.id = :id
          AND i.activo = true
        """)
    Optional<Intercambio> buscarActivoPorId(
            @Param("id") Long id
    );

    @Query("""
    SELECT i
    FROM Intercambio i
    WHERE i.activo = true
    """)
    Page<Intercambio> buscarTodosActivos(Pageable pageable);

    @Query("""
    SELECT i
    FROM Intercambio i
    WHERE i.activo = true
      AND LOWER(COALESCE(i.tituloPublicacionSnapshot, ''))
          LIKE LOWER(CONCAT('%', :busqueda, '%'))
    """)
    Page<Intercambio> buscarPorTexto(
            @Param("busqueda") String busqueda,
            Pageable pageable
    );

    @Query("""
    SELECT i
    FROM Intercambio i
    WHERE i.activo = true
      AND (:idUsuario IS NULL
           OR i.idUsuarioProponente = :idUsuario
           OR i.idUsuarioReceptor = :idUsuario)
      AND (:estado IS NULL OR i.estado = :estado)
      AND (
          CAST(:busqueda AS String) IS NULL
          OR LOWER(COALESCE(i.tituloPublicacionSnapshot, ''))
             LIKE CONCAT('%', LOWER(CAST(:busqueda AS String)), '%')
      )
      AND (CAST(:fechaDesde AS LocalDateTime) IS NULL
           OR i.fechaCreacion >= :fechaDesde)
      AND (CAST(:fechaHasta AS LocalDateTime) IS NULL
           OR i.fechaCreacion < :fechaHasta)
    """)
    Page<Intercambio> buscarConFiltros(
            @Param("idUsuario") Long idUsuario,
            @Param("estado") EstadoIntercambio estado,
            @Param("busqueda") String busqueda,
            @Param("fechaDesde") LocalDateTime fechaDesde,
            @Param("fechaHasta") LocalDateTime fechaHasta,
            Pageable pageable
    );
}