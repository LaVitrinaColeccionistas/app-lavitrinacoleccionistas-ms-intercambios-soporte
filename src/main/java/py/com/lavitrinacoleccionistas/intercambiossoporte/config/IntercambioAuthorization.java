package py.com.lavitrinacoleccionistas.intercambiossoporte.config;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import py.com.lavitrinacoleccionistas.entity.Intercambio;
import py.com.lavitrinacoleccionistas.intercambiossoporte.exception.ResourceNotFoundException;
import py.com.lavitrinacoleccionistas.intercambiossoporte.repository.IIntercambioRepository;

@Component("intercambioAuthorization")
@RequiredArgsConstructor
public class IntercambioAuthorization {

    private final IIntercambioRepository repository;

    public boolean puedeConsultar(Long id) {
        Intercambio intercambio = repository.buscarActivoPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Intercambio no encontrado con id: " + id));

        if (UsuarioAutenticado.esAdmin()) {
            return true;
        }

        Long usuarioId = UsuarioAutenticado.id();

        return usuarioId.equals(intercambio.getIdUsuarioProponente())
                || usuarioId.equals(intercambio.getIdUsuarioReceptor());
    }
}