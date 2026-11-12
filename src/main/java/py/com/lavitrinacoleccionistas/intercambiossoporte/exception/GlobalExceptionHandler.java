package py.com.lavitrinacoleccionistas.intercambiossoporte.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler
        implements AuthenticationEntryPoint, AccessDeniedHandler {

    private final HandlerExceptionResolver resolver;

    public GlobalExceptionHandler(
            @Lazy @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver
    ) {
        this.resolver = resolver;
    }

    // 401 cortado por el filtro de seguridad: se delega a los @ExceptionHandler
    @Override
    public void commence(
            HttpServletRequest req,
            HttpServletResponse res,
            AuthenticationException ex
    ) throws IOException {
        resolver.resolveException(req, res, null, ex);
    }

    // 403 cortado por el filtro de seguridad: se delega a los @ExceptionHandler
    @Override
    public void handle(
            HttpServletRequest req,
            HttpServletResponse res,
            AccessDeniedException ex
    ) throws IOException {
        resolver.resolveException(req, res, null, ex);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiError> handleAuthentication(
            AuthenticationException ex,
            HttpServletRequest req
    ) {
        log.warn("No autenticado: {}", ex.getMessage());

        return build(
                HttpStatus.UNAUTHORIZED,
                "No autenticado",
                "Token ausente, inválido o expirado",
                req,
                null
        );
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(
            ResourceNotFoundException ex,
            HttpServletRequest req
    ) {
        log.warn("Recurso no encontrado: {}", ex.getMessage());

        return build(
                HttpStatus.NOT_FOUND,
                "Recurso no encontrado",
                ex.getMessage(),
                req,
                null
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest req
    ) {
        Map<String, String> errors = new LinkedHashMap<>();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        errors.put(
                                error.getField(),
                                error.getDefaultMessage()
                        )
                );

        log.warn("Error de validación: {}", errors);

        return build(
                HttpStatus.BAD_REQUEST,
                "Datos inválidos",
                "Uno o más campos de la solicitud son inválidos",
                req,
                errors
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest req
    ) {
        return build(
                HttpStatus.BAD_REQUEST,
                "Parámetro inválido",
                "El parámetro '" + ex.getName() + "' tiene un valor inválido",
                req,
                null
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleUnreadable(
            HttpMessageNotReadableException ex,
            HttpServletRequest req
    ) {
        return build(
                HttpStatus.BAD_REQUEST,
                "Solicitud inválida",
                "El cuerpo de la solicitud es inválido o tiene valores no permitidos",
                req,
                null
        );
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiError> handleMethodNotAllowed(
            HttpRequestMethodNotSupportedException ex,
            HttpServletRequest req
    ) {
        return build(
                HttpStatus.METHOD_NOT_ALLOWED,
                "Método no permitido",
                "El método HTTP no está permitido para esta ruta",
                req,
                null
        );
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiError> handleRouteNotFound(
            NoResourceFoundException ex,
            HttpServletRequest req
    ) {
        return build(
                HttpStatus.NOT_FOUND,
                "Recurso no encontrado",
                "La ruta solicitada no existe",
                req,
                null
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrity(
            DataIntegrityViolationException ex,
            HttpServletRequest req
    ) {
        log.warn("Violación de integridad de datos", ex);

        return build(
                HttpStatus.BAD_REQUEST,
                "Datos inválidos",
                "Los datos enviados no cumplen las restricciones requeridas",
                req,
                null
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDenied(
            AccessDeniedException ex,
            HttpServletRequest req
    ) {
        log.warn("Acceso denegado: {}", ex.getMessage());

        return build(
                HttpStatus.FORBIDDEN,
                "Acceso denegado",
                "No tiene permisos para realizar esta operación",
                req,
                null
        );
    }

    @ExceptionHandler({
            IllegalArgumentException.class,
            IllegalStateException.class
    })
    public ResponseEntity<ApiError> handleInvalidOperation(
            RuntimeException ex,
            HttpServletRequest req
    ) {
        log.warn("Operación inválida: {}", ex.getMessage());

        return build(
                HttpStatus.BAD_REQUEST,
                "Solicitud inválida",
                ex.getMessage(),
                req,
                null
        );
    }

    @ExceptionHandler(RestClientResponseException.class)
    public ResponseEntity<ApiError> handleCatalogoHttp(
            RestClientResponseException ex,
            HttpServletRequest req
    ) {
        int codigo = ex.getStatusCode().value();

        HttpStatus status = switch (codigo) {
            case 400 -> HttpStatus.BAD_REQUEST;
            case 403 -> HttpStatus.FORBIDDEN;
            case 404 -> HttpStatus.NOT_FOUND;
            case 409 -> HttpStatus.CONFLICT;
            default -> HttpStatus.BAD_GATEWAY;
        };

        log.warn("Catálogo respondió HTTP {}", codigo);

        return build(
                status,
                "Error de Catálogo",
                "Catálogo rechazó la operación solicitada",
                req,
                null
        );
    }

    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<ApiError> handleCatalogoConnection(
            ResourceAccessException ex,
            HttpServletRequest req
    ) {
        log.error("Error de conexión con Catálogo", ex);

        return build(
                HttpStatus.SERVICE_UNAVAILABLE,
                "Catálogo no disponible",
                "No se pudo completar la comunicación con Catálogo",
                req,
                null
        );
    }

    @ExceptionHandler(RestClientException.class)
    public ResponseEntity<ApiError> handleCatalogoClient(
            RestClientException ex,
            HttpServletRequest req
    ) {
        log.error("Error en cliente HTTP de Catálogo", ex);

        return build(
                HttpStatus.BAD_GATEWAY,
                "Error de comunicación",
                "Respuesta inválida del servicio de Catálogo",
                req,
                null
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(
            Exception ex,
            HttpServletRequest req
    ) {
        log.error(
                "Error interno no controlado en {}",
                req.getRequestURI(),
                ex
        );

        return build(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Error interno del servidor",
                "Ocurrió un error inesperado",
                req,
                null
        );
    }

    private ResponseEntity<ApiError> build(
            HttpStatus status,
            String error,
            String message,
            HttpServletRequest req,
            Map<String, String> errors
    ) {
        ApiError response = new ApiError(
                LocalDateTime.now(),
                status.value(),
                error,
                message,
                req.getRequestURI(),
                errors
        );

        return ResponseEntity
                .status(status)
                .body(response);
    }
}