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
import py.com.lavitrinacoleccionistas.dto.IntercambioCreateDTO;
import py.com.lavitrinacoleccionistas.dto.IntercambioDTO;
import py.com.lavitrinacoleccionistas.dto.IntercambioEstadoUpdateDTO;
import py.com.lavitrinacoleccionistas.dto.IntercambioUpdateDTO;
import py.com.lavitrinacoleccionistas.enums.EstadoIntercambio;
import py.com.lavitrinacoleccionistas.intercambiossoporte.api.IIntercambioApi;
import py.com.lavitrinacoleccionistas.intercambiossoporte.service.IIntercambioService;

@Slf4j
@RestController
@RequestMapping("/intercambios")
@RequiredArgsConstructor
public class IntercambioController implements IIntercambioApi {

    private final IIntercambioService intercambioService;

    @Override
    @PostMapping
    public ResponseEntity<IntercambioDTO> crear(
            @Valid @RequestBody IntercambioCreateDTO dto
    ) {
        log.info(
                "Solicitud recibida para crear intercambio. Proponente: {}, receptor: {}",
                dto.getIdUsuarioProponente(),
                dto.getIdUsuarioReceptor()
        );

        IntercambioDTO creado = intercambioService.crear(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(creado);
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<IntercambioDTO> obtenerPorId(
            @PathVariable Long id
    ) {
        log.info(
                "Solicitud recibida para obtener intercambio con id: {}",
                id
        );

        return ResponseEntity.ok(
                intercambioService.obtenerPorId(id)
        );
    }

    @Override
    @GetMapping
    public ResponseEntity<Page<IntercambioDTO>> listar(
            @RequestParam(required = false) Long idUsuario,
            @RequestParam(required = false) EstadoIntercambio estado,
            @ParameterObject Pageable pageable
    ) {
        log.info(
                "Solicitud recibida para listar intercambios. Usuario: {}, estado: {}, página: {}, tamaño: {}",
                idUsuario,
                estado,
                pageable.getPageNumber(),
                pageable.getPageSize()
        );

        return ResponseEntity.ok(
                intercambioService.buscar(
                        idUsuario,
                        estado,
                        pageable
                )
        );
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<IntercambioDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody IntercambioUpdateDTO dto
    ) {
        log.info(
                "Solicitud recibida para actualizar intercambio con id: {}",
                id
        );

        return ResponseEntity.ok(
                intercambioService.actualizar(id, dto)
        );
    }

    @Override
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

        return ResponseEntity.ok(
                intercambioService.actualizarEstado(id, dto)
        );
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id
    ) {
        log.info(
                "Solicitud recibida para eliminar lógicamente intercambio con id: {}",
                id
        );

        intercambioService.eliminar(id);

        return ResponseEntity.noContent().build();
    }
}