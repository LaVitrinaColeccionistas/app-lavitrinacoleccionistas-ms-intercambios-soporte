package py.com.lavitrinacoleccionistas.intercambiossoporte.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import py.com.lavitrinacoleccionistas.dto.IntercambioCreateDTO;
import py.com.lavitrinacoleccionistas.dto.IntercambioDTO;
import py.com.lavitrinacoleccionistas.dto.IntercambioEstadoUpdateDTO;
import py.com.lavitrinacoleccionistas.dto.IntercambioUpdateDTO;
import py.com.lavitrinacoleccionistas.enums.EstadoIntercambio;

@Tag(
        name = "Intercambios",
        description = "Operaciones para la gestión de intercambios entre usuarios"
)
public interface IIntercambioApi {

    @Operation(
            summary = "Crear intercambio",
            description = "Registra una nueva propuesta de intercambio entre dos usuarios."
    )
    ResponseEntity<IntercambioDTO> crear(
            IntercambioCreateDTO dto
    );

    @Operation(
            summary = "Obtener intercambio por ID",
            description = "Obtiene los datos de un intercambio activo mediante su identificador."
    )
    ResponseEntity<IntercambioDTO> obtenerPorId(
            @Parameter(
                    description = "Identificador del intercambio",
                    example = "1"
            )
            Long id
    );

    @Operation(
            summary = "Listar o buscar intercambios",
            description = "Obtiene una lista paginada de intercambios activos. Permite filtrar opcionalmente por usuario y estado."
    )
    ResponseEntity<Page<IntercambioDTO>> listar(
            @Parameter(
                    description = "Identificador del usuario, como proponente o receptor",
                    example = "1"
            )
            Long idUsuario,

            @Parameter(description = "Estado del intercambio")
            EstadoIntercambio estado,

            Pageable pageable
    );

    @Operation(
            summary = "Actualizar intercambio",
            description = "Actualiza los datos y productos asociados a una propuesta de intercambio."
    )
    ResponseEntity<IntercambioDTO> actualizar(
            @Parameter(
                    description = "Identificador del intercambio",
                    example = "1"
            )
            Long id,
            IntercambioUpdateDTO dto
    );

    @Operation(
            summary = "Actualizar estado del intercambio",
            description = "Modifica el estado de un intercambio existente."
    )
    ResponseEntity<IntercambioDTO> actualizarEstado(
            @Parameter(
                    description = "Identificador del intercambio",
                    example = "1"
            )
            Long id,
            IntercambioEstadoUpdateDTO dto
    );

    @Operation(
            summary = "Eliminar intercambio",
            description = "Realiza la eliminación lógica del intercambio, marcándolo como inactivo."
    )
    ResponseEntity<Void> eliminar(
            @Parameter(
                    description = "Identificador del intercambio",
                    example = "1"
            )
            Long id
    );
}