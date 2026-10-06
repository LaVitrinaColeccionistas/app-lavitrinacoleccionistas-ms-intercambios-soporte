package py.com.lavitrinacoleccionistas.intercambiossoporte.mapper;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;

/**
 * Conversión de fechas compartida por los mappers (entity LocalDateTime -> DTO OffsetDateTime).
 */
public interface FechaMapper {

    default OffsetDateTime map(LocalDateTime value) {
        if (value == null) {
            return null;
        }

        return value
                .atZone(ZoneId.systemDefault())
                .toOffsetDateTime();
    }
}
