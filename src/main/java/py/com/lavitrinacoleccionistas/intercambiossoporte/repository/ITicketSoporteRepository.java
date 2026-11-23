package py.com.lavitrinacoleccionistas.intercambiossoporte.repository;

import org.springframework.stereotype.Repository;
import py.com.lavitrinacoleccionistas.entity.TicketSoporte;

import java.util.List;

@Repository
public interface ITicketSoporteRepository
        extends IBaseRepository<TicketSoporte> {

    List<TicketSoporte> findByNotificadoAdminFalse();
}