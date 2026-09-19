package py.com.lavitrinacoleccionistas.intercambiossoporte.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import py.com.lavitrinacoleccionistas.dto.TicketSoporteCreateDTO;
import py.com.lavitrinacoleccionistas.dto.TicketSoporteDTO;
import py.com.lavitrinacoleccionistas.intercambiossoporte.service.ITicketSoporteService;

@Slf4j
@RestController
@RequestMapping("/tickets-soporte")
@RequiredArgsConstructor
@Tag(
        name = "Soporte",
        description = "Operaciones para la gestión de tickets de soporte"
)
public class TicketSoporteController {

    private final ITicketSoporteService ticketSoporteService;

    @Operation(
            summary = "Crear ticket de soporte",
            description = "Registra una nueva solicitud de soporte."
    )
    @PostMapping
    public ResponseEntity<TicketSoporteDTO> crear(
            @Valid @RequestBody TicketSoporteCreateDTO dto
    ) {

        log.info(
                "Solicitud recibida para crear ticket de soporte. Asunto: {}",
                dto.getAsunto()
        );

        TicketSoporteDTO creado =
                ticketSoporteService.crear(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(creado);
    }

    @Operation(
            summary = "Listar tickets de soporte",
            description = "Obtiene una lista paginada de los tickets de soporte."
    )
    @GetMapping
    public ResponseEntity<Page<TicketSoporteDTO>> listar(
            Pageable pageable
    ) {

        log.info(
                "Solicitud recibida para listar tickets de soporte. Página: {}, tamaño: {}",
                pageable.getPageNumber(),
                pageable.getPageSize()
        );

        return ResponseEntity.ok(
                ticketSoporteService.listar(pageable)
        );
    }
}