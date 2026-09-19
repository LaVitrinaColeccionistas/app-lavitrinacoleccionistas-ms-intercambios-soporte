package py.com.lavitrinacoleccionistas.intercambiossoporte.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import py.com.lavitrinacoleccionistas.dto.IntercambioCreateDTO;
import py.com.lavitrinacoleccionistas.dto.IntercambioDTO;
import py.com.lavitrinacoleccionistas.dto.IntercambioEstadoUpdateDTO;
import py.com.lavitrinacoleccionistas.dto.IntercambioUpdateDTO;
import py.com.lavitrinacoleccionistas.enums.EstadoIntercambio;

public interface IIntercambioService {

    IntercambioDTO crear(IntercambioCreateDTO dto);

    IntercambioDTO obtenerPorId(Long id);

    Page<IntercambioDTO> buscar(
            Long idUsuario,
            EstadoIntercambio estado,
            Pageable pageable
    );

    IntercambioDTO actualizar(Long id, IntercambioUpdateDTO dto);

    IntercambioDTO actualizarEstado(
            Long id,
            IntercambioEstadoUpdateDTO dto
    );

    void eliminar(Long id);
}