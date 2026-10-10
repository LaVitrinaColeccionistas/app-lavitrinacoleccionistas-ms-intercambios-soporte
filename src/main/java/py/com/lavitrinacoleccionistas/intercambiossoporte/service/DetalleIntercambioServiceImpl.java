package py.com.lavitrinacoleccionistas.intercambiossoporte.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import py.com.lavitrinacoleccionistas.dto.DetalleIntercambioDTO;
import py.com.lavitrinacoleccionistas.intercambiossoporte.mapper.DetalleIntercambioMapper;
import py.com.lavitrinacoleccionistas.intercambiossoporte.repository.IDetalleIntercambioRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DetalleIntercambioServiceImpl
        implements IDetalleIntercambioService {

    private final IDetalleIntercambioRepository detalleIntercambioRepository;
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

        return detalleIntercambioRepository
                .buscarPorIntercambio(idIntercambio)
                .stream()
                .map(detalleIntercambioMapper::toDTO)
                .toList();
    }
}