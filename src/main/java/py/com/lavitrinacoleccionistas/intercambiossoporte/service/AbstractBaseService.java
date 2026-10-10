package py.com.lavitrinacoleccionistas.intercambiossoporte.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import py.com.lavitrinacoleccionistas.entity.BaseEntity;
import py.com.lavitrinacoleccionistas.intercambiossoporte.exception.ResourceNotFoundException;
import py.com.lavitrinacoleccionistas.intercambiossoporte.filter.PagedFilter;
import py.com.lavitrinacoleccionistas.intercambiossoporte.mapper.BaseMapper;
import py.com.lavitrinacoleccionistas.intercambiossoporte.repository.IBaseRepository;

import java.util.Optional;

@Slf4j
public abstract class AbstractBaseService<
        E extends BaseEntity,
        D,
        F extends PagedFilter> {

    @Value("${spring.data.web.pageable.default-page-size:20}")
    private int defaultPageSize = 20;

    @Value("${spring.data.web.pageable.max-page-size:100}")
    private int maxPageSize = 100;

    @Value("${app.pagination.sort-field:id}")
    private String sortField;

    @Value("${app.pagination.sort-direction:DESC}")
    private Sort.Direction sortDirection;

    protected abstract IBaseRepository<E> getRepository();

    protected abstract BaseMapper<E, D> getMapper();

    protected abstract String getEntityName();

    protected abstract Optional<E> findActiveById(Long id);

    protected abstract Page<E> findAllFiltered(
            F filter,
            Pageable pageable
    );

    protected abstract void deactivate(E entity);

    @Transactional(readOnly = true)
    public D getById(Long id) {
        log.debug(
                "Buscando {} activo con id: {}",
                getEntityName(),
                id
        );

        return getMapper().toDTO(findActiveEntity(id));
    }

    @Transactional
    public void delete(Long id) {
        log.info(
                "Realizando eliminación lógica de {} con id: {}",
                getEntityName(),
                id
        );

        E entity = findActiveEntity(id);

        deactivate(entity);
        getRepository().save(entity);

        log.info(
                "{} con id {} marcado como inactivo",
                getEntityName(),
                id
        );
    }

    @Transactional(readOnly = true)
    public Page<D> getByFilter(F filter) {

        Pageable pageable = buildPageable(filter);

        log.debug(
                "Buscando {} con filtros. Página: {}, tamaño: {}",
                getEntityName(),
                pageable.getPageNumber(),
                pageable.getPageSize()
        );

        return findAllFiltered(filter, pageable)
                .map(getMapper()::toDTO);
    }

    protected E findActiveEntity(Long id) {
        return findActiveById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                getEntityName()
                                        + " no encontrado con id: "
                                        + id
                        )
                );
    }

    private Pageable buildPageable(F filter) {

        int page = filter.getPage() == null
                ? 0
                : Math.max(filter.getPage(), 0);

        int size = filter.getSize() == null || filter.getSize() < 1
                ? defaultPageSize
                : Math.min(filter.getSize(), maxPageSize);

        return PageRequest.of(
                page,
                size,
                Sort.by(sortDirection, sortField)
        );
    }
}