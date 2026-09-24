package py.com.lavitrinacoleccionistas.intercambiossoporte.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import py.com.lavitrinacoleccionistas.dto.DetalleIntercambioDTO;
import py.com.lavitrinacoleccionistas.intercambiossoporte.exception.ResourceNotFoundException;
import py.com.lavitrinacoleccionistas.intercambiossoporte.mapper.DetalleIntercambioMapper;
import py.com.lavitrinacoleccionistas.intercambiossoporte.repository.IDetalleIntercambioRepository;
import py.com.lavitrinacoleccionistas.intercambiossoporte.repository.IIntercambioRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DetalleIntercambioServiceImpl
        implements IDetalleIntercambioService {

    private final IDetalleIntercambioRepository detalleIntercambioRepository;
    private final IIntercambioRepository intercambioRepository;
    private final DetalleIntercambioMapper detalleIntercambioMapper;

    @Override
    @Transactional(readOnly = true)
    public List<DetalleIntercambioDTO> listarPorIntercambio(
            Long idIntercambio
    ) {
        log.debug(
                "Listando detalles del intercambio con id: {}",
                idIntercambio
        );

        intercambioRepository
                .findByIdAndActivoTrue(idIntercambio)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Intercambio no encontrado con id: "
                                        + idIntercambio
                        )
                );

        return detalleIntercambioRepository
                .findByIntercambioId(idIntercambio)
                .stream()
                .map(detalleIntercambioMapper::toDTO)
                .toList();
    }
}