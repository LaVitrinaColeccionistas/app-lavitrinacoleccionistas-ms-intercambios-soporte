package py.com.lavitrinacoleccionistas.intercambiossoporte.config;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

public final class UsuarioAutenticado {

    private UsuarioAutenticado() {}

    public static Long id() {
        var auth = SecurityContextHolder.getContext().getAuthentication();

        if (!(auth instanceof JwtAuthenticationToken jwt)) {
            throw new AccessDeniedException("Usuario no autenticado");
        }

        Object uid = jwt.getToken().getClaim("uid");

        if (uid == null) {
            throw new AccessDeniedException("Token sin identificador de usuario");
        }

        try {
            return Long.valueOf(uid.toString());
        } catch (NumberFormatException ex) {
            throw new AccessDeniedException("Identificador de usuario inválido");
        }
    }

    public static boolean esAdmin() {
        var auth = SecurityContextHolder.getContext().getAuthentication();

        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    public static boolean tieneRol(String rol) {
        var auth = SecurityContextHolder.getContext().getAuthentication();

        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_" + rol));
    }
}