
package py.com.lavitrinacoleccionistas.intercambiossoporte.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import py.com.lavitrinacoleccionistas.dto.ReservaCreateDTO;
import py.com.lavitrinacoleccionistas.dto.ReservaDTO;
import py.com.lavitrinacoleccionistas.enums.TipoOrigenReserva;

import java.util.List;

import py.com.lavitrinacoleccionistas.dto.PublicacionDTO;

/**
 * Cliente HTTP para comunicar el MS Intercambios con el MS Catálogo.
 * Permite consultar publicaciones y gestionar las reservas de productos
 * (reservar, liberar y confirmar), enviando el JWT del usuario autenticado.
 * El control del stock se realiza en Catálogo.
 */
@Component
public class CatalogoReservaClient {

    private final RestClient client;

    public CatalogoReservaClient(
            @Value("${app.catalogo.url}") String url
    ) {
        this.client = RestClient.builder()
                .baseUrl(url)
                .build();
    }

    private String token() {
        var auth = SecurityContextHolder.getContext()
                .getAuthentication();

        if (!(auth instanceof JwtAuthenticationToken jwt)) {
            throw new AccessDeniedException(
                    "Usuario no autenticado"
            );
        }

        return jwt.getToken().getTokenValue();
    }

    public List<ReservaDTO> reservar(ReservaCreateDTO dto) {
        return client.post()
                .uri("/reservas")
                .header(HttpHeaders.AUTHORIZATION,
                        "Bearer " + token())
                .body(dto)
                .retrieve()
                .body(new org.springframework.core
                        .ParameterizedTypeReference<List<ReservaDTO>>() {});
    }

    public List<ReservaDTO> liberar(Long idIntercambio) {
        return client.post()
                .uri("/reservas/{origen}/{id}/liberar",
                        TipoOrigenReserva.INTERCAMBIO,
                        idIntercambio)
                .header(HttpHeaders.AUTHORIZATION,
                        "Bearer " + token())
                .retrieve()
                .body(new org.springframework.core
                        .ParameterizedTypeReference<List<ReservaDTO>>() {});
    }

    public List<ReservaDTO> confirmar(Long idIntercambio) {
        return client.post()
                .uri("/reservas/{origen}/{id}/confirmar",
                        TipoOrigenReserva.INTERCAMBIO,
                        idIntercambio)
                .header(HttpHeaders.AUTHORIZATION,
                        "Bearer " + token())
                .retrieve()
                .body(new org.springframework.core
                        .ParameterizedTypeReference<List<ReservaDTO>>() {});
    }

    public PublicacionDTO obtenerPublicacion(Long id) {
        return client.get()
                .uri("/publicaciones/{id}", id)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token())
                .retrieve()
                .body(PublicacionDTO.class);
    }

    public List<ReservaDTO> actualizarReserva(
            Long idIntercambio,
            ReservaCreateDTO dto
    ) {
        return client.put()
                .uri("/reservas/INTERCAMBIO/{id}", idIntercambio)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token())
                .body(dto)
                .retrieve()
                .body(new org.springframework.core
                        .ParameterizedTypeReference<List<ReservaDTO>>() {});
    }
}
