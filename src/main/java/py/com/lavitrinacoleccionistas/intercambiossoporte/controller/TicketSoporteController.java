package py.com.lavitrinacoleccionistas.intercambiossoporte.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import py.com.lavitrinacoleccionistas.dto.TicketSoporteCreateDTO;
import py.com.lavitrinacoleccionistas.dto.TicketSoporteDTO;
import py.com.lavitrinacoleccionistas.intercambiossoporte.api.ITicketSoporteApi;
import py.com.lavitrinacoleccionistas.intercambiossoporte.service.ITicketSoporteService;

@Slf4j
@RestController
@RequestMapping("/tickets-soporte")
@RequiredArgsConstructor
public class TicketSoporteController implements ITicketSoporteApi {

    private final ITicketSoporteService ticketSoporteService;

    @Override
    @PostMapping
    public ResponseEntity<TicketSoporteDTO> crear(
            @Valid @RequestBody TicketSoporteCreateDTO dto
    ) {
        log.info(
                "Solicitud recibida para crear ticket de soporte. Asunto: {}",
                dto.getAsunto()
        );

        TicketSoporteDTO creado = ticketSoporteService.crear(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(creado);
    }

    @Override
    @GetMapping
    public ResponseEntity<Page<TicketSoporteDTO>> listar(
            @ParameterObject Pageable pageable
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