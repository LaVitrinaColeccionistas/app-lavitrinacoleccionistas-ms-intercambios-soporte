package py.com.lavitrinacoleccionistas.intercambiossoporte.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String mensaje) {
        super(mensaje);
    }
}