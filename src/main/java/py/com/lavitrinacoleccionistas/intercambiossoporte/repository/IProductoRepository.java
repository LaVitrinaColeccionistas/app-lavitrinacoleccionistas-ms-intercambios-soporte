package py.com.lavitrinacoleccionistas.intercambiossoporte.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import py.com.lavitrinacoleccionistas.entity.Producto;

@Repository
public interface IProductoRepository
        extends JpaRepository<Producto, Long> {
}