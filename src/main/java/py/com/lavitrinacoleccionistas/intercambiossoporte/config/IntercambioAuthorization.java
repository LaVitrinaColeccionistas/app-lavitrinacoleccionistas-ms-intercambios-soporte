package py.com.lavitrinacoleccionistas.intercambiossoporte.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;
import py.com.lavitrinacoleccionistas.dto.IntercambioDTO;
import py.com.lavitrinacoleccionistas.entity.Intercambio;
import py.com.lavitrinacoleccionistas.intercambiossoporte.exception.ResourceNotFoundException;
import py.com.lavitrinacoleccionistas.intercambiossoporte.repository.IIntercambioRepository;

@Slf4j
@Component("intercambioAuthorization")
@RequiredArgsConstructor
public class IntercambioAuthorization {

    private final IIntercambioRepository repository;
    private final CacheManager cacheManager;

    public boolean puedeConsultar(Long id) {
        Long proponente;
        Long receptor;

        IntercambioDTO cacheado = buscarEnCache(id);

        if (cacheado != null) {
            proponente = cacheado.getIdUsuarioProponente();
            receptor = cacheado.getIdUsuarioReceptor();
        } else {
            Intercambio intercambio = repository.buscarActivoPorId(id)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Intercambio no encontrado con id: " + id));
            proponente = intercambio.getIdUsuarioProponente();
            receptor = intercambio.getIdUsuarioReceptor();
        }

        if (UsuarioAutenticado.esAdmin()) {
            return true;
        }

        Long usuarioId = UsuarioAutenticado.id();

        return usuarioId.equals(proponente) || usuarioId.equals(receptor);
    }

    private IntercambioDTO buscarEnCache(Long id) {
        try {
            Cache cache = cacheManager.getCache("intercambios");
            return cache == null ? null : cache.get(id, IntercambioDTO.class);
        } catch (RuntimeException ex) {
            log.warn("CACHE ERROR intercambios::{} - {}", id, ex.getMessage());
            return null;
        }
    }
}