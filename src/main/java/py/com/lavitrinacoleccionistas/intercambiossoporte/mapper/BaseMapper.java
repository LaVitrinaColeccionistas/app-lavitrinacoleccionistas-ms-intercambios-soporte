package py.com.lavitrinacoleccionistas.intercambiossoporte.mapper;

import py.com.lavitrinacoleccionistas.entity.BaseEntity;

public interface BaseMapper<E extends BaseEntity, D> {

    D toDTO(E entity);
}
