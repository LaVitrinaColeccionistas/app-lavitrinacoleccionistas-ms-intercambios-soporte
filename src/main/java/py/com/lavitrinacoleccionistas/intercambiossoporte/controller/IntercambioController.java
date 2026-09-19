package py.com.lavitrinacoleccionistas.intercambiossoporte.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import py.com.lavitrinacoleccionistas.intercambiossoporte.service.IIntercambioService;

@Slf4j
@RestController
@RequestMapping("/intercambios")
@RequiredArgsConstructor
@Tag(
        name = "Intercambios",
        description = "Operaciones para la gestión de intercambios entre usuarios"
)
public class IntercambioController {

    private final IIntercambioService intercambioService;

    @Operation(
            summary = "Crear intercambio",
            description = "Registra una nueva propuesta de intercambio entre dos usuarios."
    )
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

    @Operation(
            summary = "Obtener intercambio por ID",
            description = "Obtiene los datos de un intercambio activo mediante su identificador."
    )
    @GetMapping("/{id}")
    public ResponseEntity<IntercambioDTO> obtenerPorId(
            @Parameter(
                    description = "Identificador del intercambio",
                    example = "1"
            )
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

    @Operation(
            summary = "Listar o buscar intercambios",
            description = "Obtiene una lista paginada de intercambios activos. " +
                    "Permite filtrar opcionalmente por usuario y estado."
    )
    @GetMapping
    public ResponseEntity<Page<IntercambioDTO>> listar(
            @Parameter(
                    description = "Identificador del usuario, como proponente o receptor",
                    example = "1"
            )
            @RequestParam(required = false) Long idUsuario,

            @Parameter(
                    description = "Estado del intercambio"
            )
            @RequestParam(required = false) EstadoIntercambio estado,

            Pageable pageable
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

    @Operation(
            summary = "Actualizar intercambio",
            description = "Actualiza los datos y productos asociados a un intercambio existente."
    )
    @PutMapping("/{id}")
    public ResponseEntity<IntercambioDTO> actualizar(
            @Parameter(
                    description = "Identificador del intercambio",
                    example = "1"
            )
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

    @Operation(
            summary = "Actualizar estado del intercambio",
            description = "Modifica el estado de un intercambio existente."
    )
    @PatchMapping("/{id}/estado")
    public ResponseEntity<IntercambioDTO> actualizarEstado(
            @Parameter(
                    description = "Identificador del intercambio",
                    example = "1"
            )
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

    @Operation(
            summary = "Eliminar intercambio",
            description = "Realiza la eliminación lógica del intercambio, marcándolo como inactivo."
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @Parameter(
                    description = "Identificador del intercambio",
                    example = "1"
            )
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