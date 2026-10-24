package py.com.lavitrinacoleccionistas.intercambiossoporte.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.security.access.AccessDeniedException;
import py.com.lavitrinacoleccionistas.intercambiossoporte.config.UsuarioAutenticado;
import org.springframework.security.access.prepost.PreAuthorize;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

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
    private final CacheManager cacheManager;

    @Override
    @PreAuthorize("@intercambioAuthorization.puedeConsultar(#id)")
    @Cacheable(value = "intercambios", key = "#id")
    public IntercambioDTO getById(Long id) {
        return super.getById(id);
    }

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
        return intercambioRepository.buscarActivoPorId(id);
    }

    @Override
    protected Page<Intercambio> findAllFiltered(
            IntercambioFilter filter,
            Pageable pageable
    ) {
        LocalDateTime fechaDesde = filter.getFechaDesde() == null
                ? null
                : filter.getFechaDesde().atStartOfDay();

        LocalDateTime fechaHasta = filter.getFechaHasta() == null
                ? null
                : filter.getFechaHasta()
                .plusDays(1)
                .atStartOfDay();

        // Sin filtros
        if (filter.getIdUsuario() == null
                && filter.getEstado() == null
                && fechaDesde == null
                && fechaHasta == null) {

            return intercambioRepository.buscarTodosActivos(pageable);
        }

        // Con filtros
        return intercambioRepository.buscarConFiltros(
                filter.getIdUsuario(),
                filter.getEstado(),
                fechaDesde,
                fechaHasta,
                pageable
        );
    }


    @Override
    protected void deactivate(Intercambio intercambio) {
        intercambio.setActivo(false);
    }

    @Override
    @CachePut(value = "intercambios", key = "#result.id")
    @Transactional
    public IntercambioDTO crear(IntercambioCreateDTO dto) {
        log.info(
                "Creando intercambio entre usuario {} y usuario {}",
                dto.getIdUsuarioProponente(),
                dto.getIdUsuarioReceptor()
        );
        if (!UsuarioAutenticado.id().equals(dto.getIdUsuarioProponente())) {
            throw new AccessDeniedException("Proponente no autorizado");
        }

        Intercambio intercambio = intercambioMapper.toEntity(dto);
        intercambio.setActivo(true);

        agregarDetalles(intercambio, dto.getDetalles());

        Intercambio guardado = intercambioRepository.save(intercambio);

        log.info("Intercambio creado con id: {}", guardado.getId());

        return intercambioMapper.toDTO(guardado);
    }

    @Override
    @CachePut(value = "intercambios", key = "#id")
    @Transactional
    public IntercambioDTO actualizar(Long id, IntercambioUpdateDTO dto) {
        log.info("Actualizando intercambio con id: {}", id);

        Intercambio intercambio = findActiveEntity(id);

        if (!UsuarioAutenticado.id().equals(intercambio.getIdUsuarioProponente())) {
            throw new AccessDeniedException("Solo el proponente puede editar");
        }

        if (intercambio.getEstado() != EstadoIntercambio.PROPUESTO) {
            throw new IllegalStateException("Solo se pueden editar intercambios propuestos");
        }

        intercambioMapper.updateEntity(dto, intercambio);

        intercambio.getDetalles().clear();
        agregarDetalles(intercambio, dto.getDetalles());

        Intercambio actualizado = intercambioRepository.save(intercambio);

        log.info("Intercambio actualizado con id: {}", id);

        return intercambioMapper.toDTO(actualizado);
    }

    @Override
    @CachePut(value = "intercambios", key = "#id")
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

        Long usuarioId = UsuarioAutenticado.id();
        EstadoIntercambio nuevoEstado =
                EstadoIntercambio.valueOf(dto.getEstado().name());

        if (intercambio.getEstado() != EstadoIntercambio.PROPUESTO) {
            throw new IllegalStateException(
                    "Solo se puede cambiar el estado de intercambios propuestos"
            );
        }

        switch (nuevoEstado) {
            case ACEPTADO, RECHAZADO -> {
                if (!UsuarioAutenticado.tieneRol("VENDEDOR")
                        || !usuarioId.equals(intercambio.getIdUsuarioReceptor())) {
                    throw new AccessDeniedException(
                            "Solo el vendedor receptor puede aceptar o rechazar"
                    );
                }
            }

            case CANCELADO -> {
                boolean esProponente =
                        usuarioId.equals(intercambio.getIdUsuarioProponente());
                boolean esReceptor =
                        usuarioId.equals(intercambio.getIdUsuarioReceptor());

                if (!esProponente && !esReceptor) {
                    throw new AccessDeniedException(
                            "Solo los participantes pueden cancelar"
                    );
                }
            }

            default -> throw new IllegalArgumentException(
                    "Cambio de estado no permitido"
            );
        }

        intercambio.setEstado(nuevoEstado);

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

    @Override
    @CacheEvict(value = "intercambios", key = "#id")
    public void delete(Long id) {
        super.delete(id);
    }


    @Override
    @Transactional(readOnly = true)
    public Page<IntercambioDTO> getByFilter(IntercambioFilter filter) {

        if (!UsuarioAutenticado.esAdmin()) {
            filter.setIdUsuario(UsuarioAutenticado.id());
        }

        Page<IntercambioDTO> intercambios = super.getByFilter(filter);

        Cache cache = cacheManager.getCache("intercambios");

        if (cache == null) {
            return intercambios;
        }

        return intercambios.map(intercambio -> {

            Long id = intercambio.getId();

            IntercambioDTO cacheado =
                    cache.get(id, IntercambioDTO.class);

            if (cacheado != null) {
                log.info("CACHE HIT intercambios::{}", id);
                return cacheado;
            }

            log.info("CACHE MISS intercambios::{}", id);

            cache.put(id, intercambio);
            log.info("CACHE SET intercambios::{}", id);

            return intercambio;
        });
    }



}