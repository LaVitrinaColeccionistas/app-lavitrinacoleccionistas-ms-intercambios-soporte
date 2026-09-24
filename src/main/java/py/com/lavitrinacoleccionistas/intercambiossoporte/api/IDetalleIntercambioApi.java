package py.com.lavitrinacoleccionistas.intercambiossoporte.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import py.com.lavitrinacoleccionistas.dto.DetalleIntercambioDTO;

import java.util.List;

@Tag(
        name = "Detalles de Intercambio",
        description = "Consulta de los productos asociados a un intercambio"
)
public interface IDetalleIntercambioApi {

    @Operation(
            summary = "Listar detalles de un intercambio",
            description = "Obtiene los productos asociados a un intercambio."
    )
    ResponseEntity<List<DetalleIntercambioDTO>> listarPorIntercambio(
            @Parameter(
                    description = "Identificador del intercambio",
                    example = "1"
            )
            Long idIntercambio
    );
}