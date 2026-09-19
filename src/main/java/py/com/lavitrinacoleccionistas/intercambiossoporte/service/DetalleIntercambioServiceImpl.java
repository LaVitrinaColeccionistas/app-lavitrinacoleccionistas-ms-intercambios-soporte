package py.com.lavitrinacoleccionistas.intercambiossoporte.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import py.com.lavitrinacoleccionistas.dto.DetalleIntercambioCreateDTO;
import py.com.lavitrinacoleccionistas.dto.DetalleIntercambioDTO;
import py.com.lavitrinacoleccionistas.entity.DetalleIntercambio;
import py.com.lavitrinacoleccionistas.entity.Intercambio;
import py.com.lavitrinacoleccionistas.entity.Producto;
import py.com.lavitrinacoleccionistas.intercambiossoporte.exception.ResourceNotFoundException;
import py.com.lavitrinacoleccionistas.intercambiossoporte.mapper.DetalleIntercambioMapper;
import py.com.lavitrinacoleccionistas.intercambiossoporte.repository.IDetalleIntercambioRepository;
import py.com.lavitrinacoleccionistas.intercambiossoporte.repository.IIntercambioRepository;
import py.com.lavitrinacoleccionistas.intercambiossoporte.repository.IProductoRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DetalleIntercambioServiceImpl
        implements IDetalleIntercambioService {

    private final IDetalleIntercambioRepository detalleIntercambioRepository;
    private final IIntercambioRepository intercambioRepository;
    private final IProductoRepository productoRepository;
    private final DetalleIntercambioMapper detalleIntercambioMapper;

    @Override
    @Transactional(readOnly = true)
    public List<DetalleIntercambioDTO> listarPorIntercambio(
            Long idIntercambio
    ) {

        log.debug(
                "Buscando detalles del intercambio con id: {}",
                idIntercambio
        );

        Intercambio intercambio = intercambioRepository
                .findByIdAndActivoTrue(idIntercambio)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Intercambio no encontrado con id: " + idIntercambio
                ));

        List<DetalleIntercambioDTO> detalles =
                detalleIntercambioRepository
                        .findByIntercambioId(intercambio.getId())
                        .stream()
                        .map(detalleIntercambioMapper::toDTO)
                        .toList();

        log.debug(
                "Se encontraron {} detalles para el intercambio {}",
                detalles.size(),
                idIntercambio
        );

        return detalles;
    }

    @Override
    @Transactional
    public DetalleIntercambioDTO agregar(
            Long idIntercambio,
            DetalleIntercambioCreateDTO dto
    ) {

        log.info(
                "Agregando producto {} al intercambio {} como {}",
                dto.getIdProducto(),
                idIntercambio,
                dto.getTipoMovimiento()
        );

        Intercambio intercambio = intercambioRepository
                .findByIdAndActivoTrue(idIntercambio)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Intercambio no encontrado con id: " + idIntercambio
                ));

        Producto producto = productoRepository
                .findById(dto.getIdProducto())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Producto no encontrado con id: " + dto.getIdProducto()
                ));

        DetalleIntercambio detalle =
                detalleIntercambioMapper.toEntity(dto);

        detalle.setIntercambio(intercambio);
        detalle.setProducto(producto);
        detalle.setNombreProductoSnapshot(producto.getNombre());

        DetalleIntercambio guardado =
                detalleIntercambioRepository.save(detalle);

        log.info(
                "Detalle {} agregado correctamente al intercambio {}",
                guardado.getId(),
                idIntercambio
        );

        return detalleIntercambioMapper.toDTO(guardado);
    }
}