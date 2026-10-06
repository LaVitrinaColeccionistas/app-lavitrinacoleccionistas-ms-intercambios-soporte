package py.com.lavitrinacoleccionistas.intercambiossoporte.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import py.com.lavitrinacoleccionistas.dto.IntercambioCreateDTO;
import py.com.lavitrinacoleccionistas.dto.IntercambioDTO;
import py.com.lavitrinacoleccionistas.dto.IntercambioUpdateDTO;
import py.com.lavitrinacoleccionistas.entity.Intercambio;


@Mapper(
        componentModel = "spring",
        uses = DetalleIntercambioMapper.class
)
public interface IntercambioMapper extends BaseMapper<Intercambio, IntercambioDTO>, FechaMapper {

    @Override
    IntercambioDTO toDTO(Intercambio entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "estado", constant = "PROPUESTO")
    @Mapping(target = "activo", ignore = true)
    @Mapping(target = "detalles", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaActualizacion", ignore = true)
    Intercambio toEntity(IntercambioCreateDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "estado", ignore = true)
    @Mapping(target = "activo", ignore = true)
    @Mapping(target = "detalles", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaActualizacion", ignore = true)
    void updateEntity(
            IntercambioUpdateDTO dto,
            @MappingTarget Intercambio entity
    );

}
