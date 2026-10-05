package py.com.lavitrinacoleccionistas.intercambiossoporte.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import py.com.lavitrinacoleccionistas.dto.IntercambioCreateDTO;
import py.com.lavitrinacoleccionistas.dto.IntercambioDTO;
import py.com.lavitrinacoleccionistas.dto.IntercambioEstadoUpdateDTO;
import py.com.lavitrinacoleccionistas.dto.IntercambioUpdateDTO;
import py.com.lavitrinacoleccionistas.entity.Intercambio;
import py.com.lavitrinacoleccionistas.intercambiossoporte.filter.IntercambioFilter;
import py.com.lavitrinacoleccionistas.intercambiossoporte.service.IntercambioServiceImpl;

@Slf4j
@RestController
@RequestMapping("/intercambios")
public class IntercambioController
        extends BaseController<
        Intercambio,
        IntercambioDTO,
        IntercambioFilter,
        IntercambioServiceImpl> {

    public IntercambioController(IntercambioServiceImpl service) {
        super(service);
    }

    @PostMapping
    public ResponseEntity<IntercambioDTO> crear(
            @Valid @RequestBody IntercambioCreateDTO dto
    ) {
        log.info(
                "Solicitud recibida para crear intercambio. Proponente: {}, receptor: {}",
                dto.getIdUsuarioProponente(),
                dto.getIdUsuarioReceptor()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.crear(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<IntercambioDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody IntercambioUpdateDTO dto
    ) {
        log.info("Solicitud recibida para actualizar intercambio con id: {}", id);

        return ResponseEntity.ok(service.actualizar(id, dto));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<IntercambioDTO> actualizarEstado(
            @PathVariable Long id,
            @Valid @RequestBody IntercambioEstadoUpdateDTO dto
    ) {
        log.info(
                "Solicitud recibida para actualizar estado del intercambio {} a {}",
                id,
                dto.getEstado()
        );

        return ResponseEntity.ok(service.actualizarEstado(id, dto));
    }
}