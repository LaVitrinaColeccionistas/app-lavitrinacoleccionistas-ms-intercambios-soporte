package py.com.lavitrinacoleccionistas.intercambiossoporte.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import py.com.lavitrinacoleccionistas.dto.DetalleIntercambioCreateDTO;
import py.com.lavitrinacoleccionistas.dto.DetalleIntercambioDTO;
import py.com.lavitrinacoleccionistas.entity.DetalleIntercambio;

@Mapper(componentModel = "spring")
public interface DetalleIntercambioMapper {

    @Mapping(target = "idIntercambio", source = "intercambio.id")
    @Mapping(target = "idProducto", source = "producto.id")
    DetalleIntercambioDTO toDTO(DetalleIntercambio entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "intercambio", ignore = true)
    @Mapping(target = "producto", ignore = true)
    @Mapping(target = "nombreProductoSnapshot", ignore = true)
    DetalleIntercambio toEntity(DetalleIntercambioCreateDTO dto);
}