package py.com.lavitrinacoleccionistas.intercambiossoporte.service;

import py.com.lavitrinacoleccionistas.dto.IntercambioCreateDTO;
import py.com.lavitrinacoleccionistas.dto.IntercambioDTO;
import py.com.lavitrinacoleccionistas.dto.IntercambioEstadoUpdateDTO;
import py.com.lavitrinacoleccionistas.dto.IntercambioUpdateDTO;


public interface IIntercambioService {

    IntercambioDTO crear(IntercambioCreateDTO dto);

    IntercambioDTO actualizar(Long id, IntercambioUpdateDTO dto);

    IntercambioDTO actualizarEstado(Long id, IntercambioEstadoUpdateDTO dto);
}