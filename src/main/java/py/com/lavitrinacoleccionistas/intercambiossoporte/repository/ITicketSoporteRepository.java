package py.com.lavitrinacoleccionistas.intercambiossoporte.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import py.com.lavitrinacoleccionistas.entity.TicketSoporte;

@Repository
public interface ITicketSoporteRepository
        extends JpaRepository<TicketSoporte, Long>,
        JpaSpecificationExecutor<TicketSoporte> {
}