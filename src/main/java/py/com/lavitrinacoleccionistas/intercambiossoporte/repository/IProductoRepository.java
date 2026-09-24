package py.com.lavitrinacoleccionistas.intercambiossoporte.repository;

import org.springframework.stereotype.Repository;
import py.com.lavitrinacoleccionistas.entity.Producto;

@Repository
public interface IProductoRepository
        extends IBaseRepository<Producto> {
}