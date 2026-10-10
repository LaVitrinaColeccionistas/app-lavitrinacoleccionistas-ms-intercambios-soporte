package py.com.lavitrinacoleccionistas.intercambiossoporte.config;

import org.mapstruct.MapperConfig;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import py.com.lavitrinacoleccionistas.intercambiossoporte.mapper.FechaMapper;

@MapperConfig(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {FechaMapper.class}
)
public interface IBaseMapperConfig {
}