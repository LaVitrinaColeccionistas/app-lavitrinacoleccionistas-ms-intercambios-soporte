package py.com.lavitrinacoleccionistas.intercambiossoporte.controller;

import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import py.com.lavitrinacoleccionistas.entity.BaseEntity;
import py.com.lavitrinacoleccionistas.intercambiossoporte.filter.PagedFilter;
import py.com.lavitrinacoleccionistas.intercambiossoporte.service.AbstractBaseService;

@Slf4j
public abstract class BaseController<
        E extends BaseEntity,
        D,
        F extends PagedFilter,
        S extends AbstractBaseService<E, D, F>> {

    protected final S service;

    protected BaseController(S service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public ResponseEntity<D> getById(@PathVariable Long id) {
        log.info("Solicitud recibida para obtener registro con id: {}", id);

        return ResponseEntity.ok(service.getById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("Solicitud recibida para eliminar lógicamente registro con id: {}", id);

        service.delete(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<D>> getByFilter(
            @ParameterObject @ModelAttribute F filter
    ) {
        log.info("Solicitud recibida para buscar registros con filtros");

        return ResponseEntity.ok(service.getByFilter(filter));
    }
}