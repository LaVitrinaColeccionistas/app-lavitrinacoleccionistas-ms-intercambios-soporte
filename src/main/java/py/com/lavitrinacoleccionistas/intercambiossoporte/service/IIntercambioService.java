package py.com.lavitrinacoleccionistas.intercambiossoporte.service;

import org.springframework.data.domain.Page;
import py.com.lavitrinacoleccionistas.dto.IntercambioCreateDTO;
import py.com.lavitrinacoleccionistas.dto.IntercambioDTO;
import py.com.lavitrinacoleccionistas.dto.IntercambioEstadoUpdateDTO;
import py.com.lavitrinacoleccionistas.dto.IntercambioUpdateDTO;
import py.com.lavitrinacoleccionistas.intercambiossoporte.filter.IntercambioFilter;

import java.util.List;

public interface IIntercambioService {

    IntercambioDTO crear(IntercambioCreateDTO dto);

    IntercambioDTO getById(Long id);

    List<IntercambioDTO> getAll();

    Page<IntercambioDTO> getByFilter(IntercambioFilter filter);

    IntercambioDTO actualizar(Long id, IntercambioUpdateDTO dto);

    IntercambioDTO actualizarEstado(Long id, IntercambioEstadoUpdateDTO dto);

    void delete(Long id);
}