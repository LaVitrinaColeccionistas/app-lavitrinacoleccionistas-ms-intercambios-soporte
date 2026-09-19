package py.com.lavitrinacoleccionistas.intercambiossoporte.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import py.com.lavitrinacoleccionistas.dto.DetalleIntercambioCreateDTO;
import py.com.lavitrinacoleccionistas.dto.IntercambioCreateDTO;
import py.com.lavitrinacoleccionistas.dto.IntercambioDTO;
import py.com.lavitrinacoleccionistas.dto.IntercambioEstadoUpdateDTO;
import py.com.lavitrinacoleccionistas.dto.IntercambioUpdateDTO;
import py.com.lavitrinacoleccionistas.entity.DetalleIntercambio;
import py.com.lavitrinacoleccionistas.entity.Intercambio;
import py.com.lavitrinacoleccionistas.entity.Producto;
import py.com.lavitrinacoleccionistas.enums.EstadoIntercambio;
import py.com.lavitrinacoleccionistas.intercambiossoporte.exception.ResourceNotFoundException;
import py.com.lavitrinacoleccionistas.intercambiossoporte.mapper.DetalleIntercambioMapper;
import py.com.lavitrinacoleccionistas.intercambiossoporte.mapper.IntercambioMapper;
import py.com.lavitrinacoleccionistas.intercambiossoporte.repository.IIntercambioRepository;
import py.com.lavitrinacoleccionistas.intercambiossoporte.repository.IProductoRepository;
import py.com.lavitrinacoleccionistas.intercambiossoporte.specification.IntercambioSpecification;

@Slf4j
@Service
@RequiredArgsConstructor
public class IntercambioServiceImpl implements IIntercambioService {

    private final IIntercambioRepository intercambioRepository;
    private final IProductoRepository productoRepository;
    private final IntercambioMapper intercambioMapper;
    private final DetalleIntercambioMapper detalleIntercambioMapper;

    @Override
    @Transactional
    public IntercambioDTO crear(IntercambioCreateDTO dto) {

        log.info(
                "Iniciando creación de intercambio. Proponente: {}, receptor: {}",
                dto.getIdUsuarioProponente(),
                dto.getIdUsuarioReceptor()
        );

        Intercambio intercambio = intercambioMapper.toEntity(dto);

        if (dto.getDetalles() != null) {

            log.debug(
                    "Procesando {} detalles para el nuevo intercambio",
                    dto.getDetalles().size()
            );

            for (DetalleIntercambioCreateDTO detalleDTO : dto.getDetalles()) {

                log.debug(
                        "Buscando producto con id: {}",
                        detalleDTO.getIdProducto()
                );

                Producto producto = productoRepository
                        .findById(detalleDTO.getIdProducto())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Producto no encontrado con id: "
                                        + detalleDTO.getIdProducto()
                        ));

                DetalleIntercambio detalle =
                        detalleIntercambioMapper.toEntity(detalleDTO);

                detalle.setIntercambio(intercambio);
                detalle.setProducto(producto);
                detalle.setNombreProductoSnapshot(producto.getNombre());

                intercambio.getDetalles().add(detalle);
            }
        }

        Intercambio guardado =
                intercambioRepository.save(intercambio);

        log.info(
                "Intercambio creado correctamente con id: {}",
                guardado.getId()
        );

        return intercambioMapper.toDTO(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public IntercambioDTO obtenerPorId(Long id) {

        log.debug("Buscando intercambio con id: {}", id);

        Intercambio intercambio = intercambioRepository
                .findByIdAndActivoTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Intercambio no encontrado con id: " + id
                ));

        return intercambioMapper.toDTO(intercambio);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<IntercambioDTO> buscar(
            Long idUsuario,
            EstadoIntercambio estado,
            Pageable pageable
    ) {

        log.debug(
                "Buscando intercambios. Usuario: {}, estado: {}",
                idUsuario,
                estado
        );

        Specification<Intercambio> specification =
                IntercambioSpecification.estaActivo()
                        .and(IntercambioSpecification
                                .tieneUsuario(idUsuario))
                        .and(IntercambioSpecification
                                .tieneEstado(estado));

        return intercambioRepository
                .findAll(specification, pageable)
                .map(intercambioMapper::toDTO);
    }

    @Override
    @Transactional
    public IntercambioDTO actualizar(
            Long id,
            IntercambioUpdateDTO dto
    ) {

        log.info("Iniciando actualización del intercambio con id: {}", id);

        Intercambio intercambio = intercambioRepository
                .findByIdAndActivoTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Intercambio no encontrado con id: " + id
                ));

        intercambioMapper.updateEntity(dto, intercambio);

        intercambio.getDetalles().clear();

        log.debug(
                "Reemplazando detalles del intercambio {}. Cantidad nueva: {}",
                id,
                dto.getDetalles().size()
        );

        for (DetalleIntercambioCreateDTO detalleDTO : dto.getDetalles()) {

            Producto producto = productoRepository
                    .findById(detalleDTO.getIdProducto())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Producto no encontrado con id: "
                                    + detalleDTO.getIdProducto()
                    ));

            DetalleIntercambio detalle =
                    detalleIntercambioMapper.toEntity(detalleDTO);

            detalle.setIntercambio(intercambio);
            detalle.setProducto(producto);
            detalle.setNombreProductoSnapshot(producto.getNombre());

            intercambio.getDetalles().add(detalle);
        }

        Intercambio actualizado =
                intercambioRepository.save(intercambio);

        log.info(
                "Intercambio actualizado correctamente con id: {}",
                actualizado.getId()
        );

        return intercambioMapper.toDTO(actualizado);
    }

    @Override
    @Transactional
    public IntercambioDTO actualizarEstado(
            Long id,
            IntercambioEstadoUpdateDTO dto
    ) {

        log.info(
                "Actualizando estado del intercambio {} a {}",
                id,
                dto.getEstado()
        );

        Intercambio intercambio = intercambioRepository
                .findByIdAndActivoTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Intercambio no encontrado con id: " + id
                ));

        intercambio.setEstado(
                EstadoIntercambio.valueOf(dto.getEstado().name())
        );

        Intercambio actualizado =
                intercambioRepository.save(intercambio);

        log.info(
                "Estado del intercambio {} actualizado correctamente a {}",
                id,
                actualizado.getEstado()
        );

        return intercambioMapper.toDTO(actualizado);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {

        log.info(
                "Iniciando eliminación lógica del intercambio con id: {}",
                id
        );

        Intercambio intercambio = intercambioRepository
                .findByIdAndActivoTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Intercambio no encontrado con id: " + id
                ));

        intercambio.setActivo(false);

        intercambioRepository.save(intercambio);

        log.info(
                "Intercambio {} eliminado lógicamente correctamente",
                id
        );
    }
}