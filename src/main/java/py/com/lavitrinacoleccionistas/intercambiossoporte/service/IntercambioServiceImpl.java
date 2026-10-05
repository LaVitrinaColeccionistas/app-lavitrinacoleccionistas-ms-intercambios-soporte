package py.com.lavitrinacoleccionistas.intercambiossoporte.service;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import py.com.lavitrinacoleccionistas.intercambiossoporte.filter.IntercambioFilter;
import py.com.lavitrinacoleccionistas.intercambiossoporte.mapper.BaseMapper;
import py.com.lavitrinacoleccionistas.intercambiossoporte.mapper.DetalleIntercambioMapper;
import py.com.lavitrinacoleccionistas.intercambiossoporte.mapper.IntercambioMapper;
import py.com.lavitrinacoleccionistas.intercambiossoporte.repository.IBaseRepository;
import py.com.lavitrinacoleccionistas.intercambiossoporte.repository.IIntercambioRepository;
import py.com.lavitrinacoleccionistas.intercambiossoporte.repository.IProductoRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class IntercambioServiceImpl
        extends AbstractBaseService<Intercambio, IntercambioDTO, IntercambioFilter>
        implements IIntercambioService {

    private final IIntercambioRepository intercambioRepository;
    private final IProductoRepository productoRepository;
    private final IntercambioMapper intercambioMapper;
    private final DetalleIntercambioMapper detalleIntercambioMapper;

    // ---- Puntos de extensión de AbstractBaseService ----

    @Override
    protected IBaseRepository<Intercambio> getRepository() {
        return intercambioRepository;
    }

    @Override
    protected BaseMapper<Intercambio, IntercambioDTO> getMapper() {
        return intercambioMapper;
    }

    @Override
    protected String getEntityName() {
        return "Intercambio";
    }

    @Override
    protected Optional<Intercambio> findActiveById(Long id) {
        return intercambioRepository.findByIdAndActivoTrue(id);
    }

    @Override
    protected Specification<Intercambio> activeSpecification() {
        return (root, query, cb) -> cb.isTrue(root.get("activo"));
    }

    @Override
    protected Specification<Intercambio> filterSpecification(IntercambioFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.getIdUsuario() != null) {
                predicates.add(cb.or(
                        cb.equal(root.get("idUsuarioProponente"), filter.getIdUsuario()),
                        cb.equal(root.get("idUsuarioReceptor"), filter.getIdUsuario())
                ));
            }

            if (filter.getEstado() != null) {
                predicates.add(cb.equal(root.get("estado"), filter.getEstado()));
            }

            if (filter.getFechaDesde() != null) {
                predicates.add(cb.greaterThanOrEqualTo(
                        root.<LocalDateTime>get("fechaCreacion"),
                        filter.getFechaDesde().atStartOfDay()
                ));
            }

            if (filter.getFechaHasta() != null) {
                predicates.add(cb.lessThan(
                        root.<LocalDateTime>get("fechaCreacion"),
                        filter.getFechaHasta().plusDays(1).atStartOfDay()
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    @Override
    protected void deactivate(Intercambio intercambio) {
        intercambio.setActivo(false);
    }

    // ---- Operaciones con DTOs de entrada propios ----

    @Override
    @Transactional
    public IntercambioDTO crear(IntercambioCreateDTO dto) {
        log.info(
                "Creando intercambio entre usuario {} y usuario {}",
                dto.getIdUsuarioProponente(),
                dto.getIdUsuarioReceptor()
        );

        Intercambio intercambio = intercambioMapper.toEntity(dto);
        intercambio.setActivo(true);

        agregarDetalles(intercambio, dto.getDetalles());

        Intercambio guardado = intercambioRepository.save(intercambio);

        log.info("Intercambio creado con id: {}", guardado.getId());

        return intercambioMapper.toDTO(guardado);
    }

    @Override
    @Transactional
    public IntercambioDTO actualizar(Long id, IntercambioUpdateDTO dto) {
        log.info("Actualizando intercambio con id: {}", id);

        Intercambio intercambio = findActiveEntity(id);

        intercambioMapper.updateEntity(dto, intercambio);

        intercambio.getDetalles().clear();
        agregarDetalles(intercambio, dto.getDetalles());

        Intercambio actualizado = intercambioRepository.save(intercambio);

        log.info("Intercambio actualizado con id: {}", id);

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

        Intercambio intercambio = findActiveEntity(id);

        intercambio.setEstado(
                EstadoIntercambio.valueOf(dto.getEstado().name())
        );

        Intercambio actualizado = intercambioRepository.save(intercambio);

        log.info(
                "Estado del intercambio {} actualizado a {}",
                id,
                actualizado.getEstado()
        );

        return intercambioMapper.toDTO(actualizado);
    }

    private void agregarDetalles(
            Intercambio intercambio,
            Iterable<DetalleIntercambioCreateDTO> detalles
    ) {
        for (DetalleIntercambioCreateDTO detalleDTO : detalles) {

            Producto producto = productoRepository
                    .findById(detalleDTO.getIdProducto())
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Producto no encontrado con id: "
                                            + detalleDTO.getIdProducto()
                            )
                    );

            DetalleIntercambio detalle =
                    detalleIntercambioMapper.toEntity(detalleDTO);

            detalle.setIntercambio(intercambio);
            detalle.setProducto(producto);
            detalle.setNombreProductoSnapshot(producto.getNombre());

            intercambio.getDetalles().add(detalle);
        }
    }
}