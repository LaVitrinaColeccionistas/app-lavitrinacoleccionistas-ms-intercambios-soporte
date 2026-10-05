package py.com.lavitrinacoleccionistas.intercambiossoporte.filter;

import lombok.Getter;
import lombok.Setter;

/**
 * Filtro base con los parámetros de paginación comunes a todas las búsquedas.
 * Si page o size no se envían, el servicio aplica los valores configurados
 * en application.yaml (spring.data.web.pageable.*).
 */
@Getter
@Setter
public class PagedFilter {

    private Integer page;

    private Integer size;
}
