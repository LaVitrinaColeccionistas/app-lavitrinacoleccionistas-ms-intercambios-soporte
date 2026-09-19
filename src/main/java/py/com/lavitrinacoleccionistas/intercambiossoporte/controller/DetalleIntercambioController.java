package py.com.lavitrinacoleccionistas.intercambiossoporte.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import py.com.lavitrinacoleccionistas.dto.DetalleIntercambioCreateDTO;
import py.com.lavitrinacoleccionistas.dto.DetalleIntercambioDTO;
import py.com.lavitrinacoleccionistas.intercambiossoporte.service.IDetalleIntercambioService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/intercambios/{idIntercambio}/detalles")
@RequiredArgsConstructor
@Tag(
        name = "Detalles de Intercambio",
        description = "Operaciones para gestionar los productos asociados a un intercambio"
)
public class DetalleIntercambioController {

    private final IDetalleIntercambioService detalleIntercambioService;

    @Operation(
            summary = "Listar detalles de un intercambio",
            description = "Obtiene los productos asociados a un intercambio."
    )
    @GetMapping
    public ResponseEntity<List<DetalleIntercambioDTO>> listarPorIntercambio(
            @Parameter(
                    description = "Identificador del intercambio",
                    example = "1"
            )
            @PathVariable Long idIntercambio
    ) {

        log.info(
                "Solicitud recibida para listar detalles del intercambio con id: {}",
                idIntercambio
        );

        return ResponseEntity.ok(
                detalleIntercambioService.listarPorIntercambio(idIntercambio)
        );
    }

    @Operation(
            summary = "Agregar detalle a un intercambio",
            description = "Agrega un producto a un intercambio existente."
    )
    @PostMapping
    public ResponseEntity<DetalleIntercambioDTO> agregar(
            @Parameter(
                    description = "Identificador del intercambio",
                    example = "1"
            )
            @PathVariable Long idIntercambio,

            @Valid @RequestBody DetalleIntercambioCreateDTO dto
    ) {

        log.info(
                "Solicitud recibida para agregar producto {} al intercambio {}",
                dto.getIdProducto(),
                idIntercambio
        );

        DetalleIntercambioDTO creado =
                detalleIntercambioService.agregar(idIntercambio, dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(creado);
    }
}