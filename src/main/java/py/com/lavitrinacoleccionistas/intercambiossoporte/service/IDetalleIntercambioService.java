package py.com.lavitrinacoleccionistas.intercambiossoporte.service;

import py.com.lavitrinacoleccionistas.dto.DetalleIntercambioCreateDTO;
import py.com.lavitrinacoleccionistas.dto.DetalleIntercambioDTO;

import java.util.List;

public interface IDetalleIntercambioService {

    List<DetalleIntercambioDTO> listarPorIntercambio(Long idIntercambio);

    DetalleIntercambioDTO agregar(
            Long idIntercambio,
            DetalleIntercambioCreateDTO dto
    );
}