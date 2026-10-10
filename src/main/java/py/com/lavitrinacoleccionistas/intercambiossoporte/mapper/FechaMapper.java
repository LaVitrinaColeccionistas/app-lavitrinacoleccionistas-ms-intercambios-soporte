package py.com.lavitrinacoleccionistas.intercambiossoporte.mapper;

import org.mapstruct.Mapper;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;

/**
 * (Entity LocalDateTime -> DTO OffsetDateTime).
 */
@Mapper(componentModel = "spring")
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