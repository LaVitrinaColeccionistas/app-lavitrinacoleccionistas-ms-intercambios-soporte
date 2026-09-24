package py.com.lavitrinacoleccionistas.intercambiossoporte.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import py.com.lavitrinacoleccionistas.dto.TicketSoporteCreateDTO;
import py.com.lavitrinacoleccionistas.dto.TicketSoporteDTO;

@Tag(
        name = "Soporte",
        description = "Operaciones para la gestión de tickets de soporte"
)
public interface ITicketSoporteApi {

    @Operation(
            summary = "Crear ticket de soporte",
            description = "Registra una nueva solicitud de soporte."
    )
    ResponseEntity<TicketSoporteDTO> crear(
            TicketSoporteCreateDTO dto
    );

    @Operation(
            summary = "Listar tickets de soporte",
            description = "Obtiene una lista paginada de los tickets de soporte."
    )
    ResponseEntity<Page<TicketSoporteDTO>> listar(
            Pageable pageable
    );
}