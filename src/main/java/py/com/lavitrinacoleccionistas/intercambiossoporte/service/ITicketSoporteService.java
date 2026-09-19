package py.com.lavitrinacoleccionistas.intercambiossoporte.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import py.com.lavitrinacoleccionistas.dto.TicketSoporteCreateDTO;
import py.com.lavitrinacoleccionistas.dto.TicketSoporteDTO;

public interface ITicketSoporteService {

    TicketSoporteDTO crear(TicketSoporteCreateDTO dto);

    Page<TicketSoporteDTO> listar(Pageable pageable);
}