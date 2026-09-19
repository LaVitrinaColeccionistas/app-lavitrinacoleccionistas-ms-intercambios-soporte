package py.com.lavitrinacoleccionistas.intercambiossoporte.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import py.com.lavitrinacoleccionistas.dto.TicketSoporteCreateDTO;
import py.com.lavitrinacoleccionistas.dto.TicketSoporteDTO;
import py.com.lavitrinacoleccionistas.entity.TicketSoporte;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;

@Mapper(componentModel = "spring")
public interface TicketSoporteMapper {

    TicketSoporteDTO toDTO(TicketSoporte entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(
            target = "fechaEnvio",
            expression = "java(java.time.LocalDateTime.now())"
    )
    TicketSoporte toEntity(TicketSoporteCreateDTO dto);

    default OffsetDateTime map(LocalDateTime value) {
        if (value == null) {
            return null;
        }

        return value
                .atZone(ZoneId.systemDefault())
                .toOffsetDateTime();
    }
}