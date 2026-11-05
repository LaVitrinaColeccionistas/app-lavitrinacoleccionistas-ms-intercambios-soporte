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

import py.com.lavitrinacoleccionistas.intercambiossoporte.client.CatalogoReservaClient;
import py.com.lavitrinacoleccionistas.dto.PublicacionDTO;
import py.com.lavitrinacoleccionistas.dto.ReservaCreateDTO;
import py.com.lavitrinacoleccionistas.dto.ReservaItemCreateDTO;
import py.com.lavitrinacoleccionistas.dto.TipoOrigenReserva;
import py.com.lavitrinacoleccionistas.dto.TipoMovimientoIntercambio;
import java.util.List;

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
    private final CatalogoReservaClient catalogoReservaClient;

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
                : filter.getFechaHasta().plusDays(1).atStartOfDay();

        String busqueda = filter.getBusqueda();

        if (busqueda != null) {
            busqueda = busqueda.trim();

            if (busqueda.isEmpty()) {
                busqueda = null;
            }
        }

        if (filter.getIdUsuario() == null
                && filter.getEstado() == null
                && busqueda == null
                && fechaDesde == null
                && fechaHasta == null) {

            return intercambioRepository.buscarTodosActivos(pageable);
        }

        return intercambioRepository.buscarConFiltros(
                filter.getIdUsuario(),
                filter.getEstado(),
                busqueda,
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

        Intercambio guardado =
                intercambioRepository.saveAndFlush(intercambio);

        reservarOfrecidos(guardado.getId(), dto);

        log.info("Intercambio creado con id: {}", guardado.getId());

        return intercambioMapper.toDTO(guardado);
    }

    @Override
    @CachePut(value = "intercambios", key = "#id")
    @Transactional
    public IntercambioDTO actualizar(Long id, IntercambioUpdateDTO dto) {

        Intercambio intercambio = findActiveEntity(id);

        if (!UsuarioAutenticado.id()
                .equals(intercambio.getIdUsuarioProponente())) {
            throw new AccessDeniedException("Solo el proponente puede editar");
        }

        if (intercambio.getEstado() != EstadoIntercambio.PROPUESTO) {
            throw new IllegalStateException(
                    "Solo se pueden editar intercambios propuestos");
        }

        if (!java.util.Objects.equals(dto.getIdUsuarioProponente(),
                intercambio.getIdUsuarioProponente())
                || !java.util.Objects.equals(dto.getIdUsuarioReceptor(),
                intercambio.getIdUsuarioReceptor())) {
            throw new IllegalArgumentException(
                    "No se pueden modificar los participantes");
        }

        if (dto.getDetalles() == null
                || dto.getDetalles().size() != intercambio.getDetalles().size()) {
            throw new IllegalArgumentException(
                    "No se pueden agregar ni eliminar productos");
        }

        var actuales = intercambio.getDetalles().stream()
                .collect(java.util.stream.Collectors.toMap(
                        d -> d.getTipoMovimiento().name()
                                + ":" + d.getProducto().getId(),
                        d -> d,
                        (a, b) -> {
                            throw new IllegalStateException("Detalle duplicado");
                        }
                ));

        var nuevos = dto.getDetalles().stream()
                .collect(java.util.stream.Collectors.toMap(
                        d -> d.getTipoMovimiento().name()
                                + ":" + d.getIdProducto(),
                        d -> d,
                        (a, b) -> {
                            throw new IllegalArgumentException("Producto duplicado");
                        }
                ));

        if (!actuales.keySet().equals(nuevos.keySet())) {
            throw new IllegalArgumentException(
                    "No se pueden cambiar los productos");
        }

        for (var entry : actuales.entrySet()) {
            var nuevo = nuevos.get(entry.getKey());

            if (nuevo.getCantidad() == null || nuevo.getCantidad() <= 0) {
                throw new IllegalArgumentException(
                        "Las cantidades deben ser mayores a cero");
            }
        }

        if (dto.getDiferenciaDinero() != null
                && (!Double.isFinite(dto.getDiferenciaDinero())
                || dto.getDiferenciaDinero() < 0)) {
            throw new IllegalArgumentException(
                    "La diferencia de dinero no puede ser negativa");
        }

        if (dto.getUsuarioPagaDiferencia() != null
                && !dto.getUsuarioPagaDiferencia()
                .equals(intercambio.getIdUsuarioProponente())
                && !dto.getUsuarioPagaDiferencia()
                .equals(intercambio.getIdUsuarioReceptor())) {
            throw new IllegalArgumentException(
                    "El pagador debe ser participante del intercambio");
        }

        List<ReservaItemCreateDTO> ofrecidos = dto.getDetalles().stream()
                .filter(d -> d.getTipoMovimiento()
                        == py.com.lavitrinacoleccionistas.dto
                        .TipoMovimientoIntercambio.OFRECIDO)
                .map(d -> new ReservaItemCreateDTO()
                        .idProducto(d.getIdProducto())
                        .cantidad(d.getCantidad())
                        .idUsuarioDestino(intercambio.getIdUsuarioReceptor()))
                .toList();

        if (ofrecidos.isEmpty()) {
            throw new IllegalArgumentException(
                    "El intercambio debe tener productos ofrecidos");
        }

        // Sincroniza las reservas ofrecidas con MS Catálogo.
        catalogoReservaClient.actualizarReserva(
                id,
                new ReservaCreateDTO()
                        .origen(TipoOrigenReserva.INTERCAMBIO)
                        .idOrigen(id)
                        .items(ofrecidos)
        );

        // Actualiza cantidades sin reemplazar los detalles existentes.
        for (var entry : actuales.entrySet()) {
            entry.getValue().setCantidad(
                    nuevos.get(entry.getKey()).getCantidad()
            );
        }

        if (dto.getDiferenciaDinero() != null) {
            intercambio.setDiferenciaDinero(
                    java.math.BigDecimal.valueOf(dto.getDiferenciaDinero())
            );
        } else {
            intercambio.setDiferenciaDinero(null);
        }

        intercambio.setUsuarioPagaDiferencia(
                dto.getUsuarioPagaDiferencia()
        );

        return intercambioMapper.toDTO(
                intercambioRepository.save(intercambio)
        );
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

        if (nuevoEstado == EstadoIntercambio.COMPLETADO) {
            if (intercambio.getEstado() != EstadoIntercambio.ACEPTADO) {
                throw new IllegalStateException(
                        "Solo se pueden completar intercambios aceptados"
                );
            }
        } else if (intercambio.getEstado() != EstadoIntercambio.PROPUESTO) {
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

            case COMPLETADO -> {
                if (!UsuarioAutenticado.tieneRol("VENDEDOR")
                        || !usuarioId.equals(intercambio.getIdUsuarioReceptor())) {
                    throw new AccessDeniedException(
                            "Solo el vendedor receptor puede completar el intercambio"
                    );
                }
            }

            default -> throw new IllegalArgumentException(
                    "Cambio de estado no permitido"
            );
        }

        switch (nuevoEstado) {
            case ACEPTADO ->
                    reservarSolicitados(intercambio);

            case RECHAZADO, CANCELADO ->
                    catalogoReservaClient.liberar(id);

            case COMPLETADO ->
                    catalogoReservaClient.confirmar(id);

            default -> throw new IllegalArgumentException(
                    "Estado no permitido"
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
            log.warn("CACHE NO DISPONIBLE: intercambios");
            return intercambios;
        }

        return intercambios.map(intercambio -> {

            Long id = intercambio.getId();

            try {
                IntercambioDTO cacheado =
                        cache.get(id, IntercambioDTO.class);

                if (cacheado != null) {
                    log.info("CACHE HIT intercambios::{}", id);
                    return cacheado;
                }

                log.info("CACHE MISS intercambios::{}", id);

                cache.put(id, intercambio);
                log.info("CACHE SET intercambios::{}", id);

            } catch (org.springframework.data.redis.RedisConnectionFailureException ex) {
                log.warn(
                        "CACHE ERROR intercambios::{} - Redis no disponible: {}",
                        id,
                        ex.getMessage()
                );
            }

            return intercambio;
        });
    }

    private void reservarSolicitados(Intercambio intercambio) {

        PublicacionDTO publicacion =
                catalogoReservaClient.obtenerPublicacion(
                        intercambio.getIdPublicacion()
                );

        if (publicacion == null) {
            throw new IllegalStateException("Publicación no encontrada");
        }

        List<ReservaItemCreateDTO> items = intercambio.getDetalles()
                .stream()
                .filter(d -> d.getTipoMovimiento()
                        == py.com.lavitrinacoleccionistas.enums.TipoMovimientoIntercambio.SOLICITADO)
                .map(d -> {
                    var detalle = publicacion.getDetalles()
                            .stream()
                            .filter(p -> p.getProducto() != null
                                    && p.getProducto().getId()
                                    .equals(d.getProducto().getId()))
                            .findFirst()
                            .orElseThrow(() ->
                                    new IllegalStateException(
                                            "Producto no pertenece a la publicación"
                                    )
                            );

                    return new ReservaItemCreateDTO()
                            .idDetallePublicacion(detalle.getId())
                            .cantidad(d.getCantidad())
                            .idUsuarioDestino(
                                    intercambio.getIdUsuarioProponente()
                            );
                })
                .toList();

        if (items.isEmpty()) {
            throw new IllegalStateException(
                    "No hay productos solicitados"
            );
        }

        catalogoReservaClient.reservar(
                new ReservaCreateDTO()
                        .origen(TipoOrigenReserva.INTERCAMBIO)
                        .idOrigen(intercambio.getId())
                        .items(items)
        );
    }

    private void reservarOfrecidos(
            Long idIntercambio,
            IntercambioCreateDTO dto
    ) {
        List<ReservaItemCreateDTO> items = dto.getDetalles()
                .stream()
                .filter(d -> d.getTipoMovimiento()
                        == TipoMovimientoIntercambio.OFRECIDO)
                .map(d -> new ReservaItemCreateDTO()
                        .idProducto(d.getIdProducto())
                        .cantidad(d.getCantidad())
                        .idUsuarioDestino(dto.getIdUsuarioReceptor()))
                .toList();

        if (items.isEmpty()) {
            throw new IllegalArgumentException(
                    "El intercambio debe incluir productos ofrecidos"
            );
        }

        catalogoReservaClient.reservar(
                new ReservaCreateDTO()
                        .origen(TipoOrigenReserva.INTERCAMBIO)
                        .idOrigen(idIntercambio)
                        .items(items)
        );
    }


}