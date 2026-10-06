package py.com.lavitrinacoleccionistas.intercambiossoporte.exception;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> manejarResourceNotFound(
            ResourceNotFoundException ex
    ) {
        log.warn("Recurso no encontrado: {}", ex.getMessage());

        return construirRespuesta(
                HttpStatus.NOT_FOUND,
                "Recurso no encontrado",
                ex.getMessage(),
                null
        );
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Map<String, Object>> manejarRutaNoEncontrada(
            NoResourceFoundException ex
    ) {
        log.warn("Ruta no encontrada: {}", ex.getResourcePath());

        return construirRespuesta(
                HttpStatus.NOT_FOUND,
                "Recurso no encontrado",
                "La ruta solicitada no existe",
                null
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> manejarValidacion(
            MethodArgumentNotValidException ex
    ) {
        Map<String, String> errores = new LinkedHashMap<>();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        errores.put(
                                error.getField(),
                                error.getDefaultMessage()
                        )
                );

        log.warn("Error de validación en la solicitud: {}", errores);

        return construirRespuesta(
                HttpStatus.BAD_REQUEST,
                "Datos inválidos",
                "Uno o más campos de la solicitud son inválidos",
                errores
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> manejarCuerpoInvalido(
            HttpMessageNotReadableException ex
    ) {
        log.warn("Cuerpo de la solicitud ilegible: {}", ex.getMessage());

        return construirRespuesta(
                HttpStatus.BAD_REQUEST,
                "Solicitud inválida",
                "El cuerpo de la solicitud es inválido o tiene valores no permitidos",
                null
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> manejarTipoInvalido(
            MethodArgumentTypeMismatchException ex
    ) {
        log.warn(
                "Parámetro con tipo inválido: {} = {}",
                ex.getName(),
                ex.getValue()
        );

        return construirRespuesta(
                HttpStatus.BAD_REQUEST,
                "Parámetro inválido",
                "El parámetro '" + ex.getName() + "' tiene un valor inválido",
                null
        );
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Map<String, Object>> manejarMetodoNoPermitido(
            HttpRequestMethodNotSupportedException ex
    ) {
        log.warn("Método HTTP no permitido: {}", ex.getMethod());

        return construirRespuesta(
                HttpStatus.METHOD_NOT_ALLOWED,
                "Método no permitido",
                "El método HTTP no está permitido para esta ruta",
                null
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> manejarIntegridadDatos(
            DataIntegrityViolationException ex
    ) {
        // El detalle técnico (SQL, constraints) solo se registra, no se expone
        log.warn("Violación de integridad de datos", ex);

        return construirRespuesta(
                HttpStatus.BAD_REQUEST,
                "Datos inválidos",
                "Los datos enviados no cumplen las restricciones requeridas",
                null
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> manejarExcepcionGeneral(
            Exception ex
    ) {
        log.error("Error interno no controlado", ex);

        return construirRespuesta(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Error interno del servidor",
                "Ocurrió un error inesperado",
                null
        );
    }

    private ResponseEntity<Map<String, Object>> construirRespuesta(
            HttpStatus status,
            String error,
            String mensaje,
            Map<String, String> errores
    ) {
        Map<String, Object> respuesta = new LinkedHashMap<>();

        respuesta.put("timestamp", LocalDateTime.now());
        respuesta.put("status", status.value());
        respuesta.put("error", error);
        respuesta.put("message", mensaje);

        if (errores != null) {
            respuesta.put("errors", errores);
        }

        return ResponseEntity.status(status).body(respuesta);
    }
}