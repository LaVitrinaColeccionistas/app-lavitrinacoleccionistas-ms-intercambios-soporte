package py.com.lavitrinacoleccionistas.intercambiossoporte.filter;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import py.com.lavitrinacoleccionistas.enums.EstadoIntercambio;

import java.time.LocalDate;

@Getter
@Setter
public class IntercambioFilter extends PagedFilter {

    private Long idUsuario;

    private EstadoIntercambio estado;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaDesde;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaHasta;
}
