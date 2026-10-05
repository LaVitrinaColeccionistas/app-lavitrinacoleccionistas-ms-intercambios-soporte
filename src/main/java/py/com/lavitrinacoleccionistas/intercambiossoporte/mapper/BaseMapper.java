package py.com.lavitrinacoleccionistas.intercambiossoporte.mapper;

import py.com.lavitrinacoleccionistas.entity.BaseEntity;

/**
 * Contrato común de conversión Entity -> DTO usado por AbstractBaseService.
 * La conversión DTO -> Entity depende de cada DTO de entrada (create/update),
 * por eso la define cada mapper concreto.
 */
public interface BaseMapper<E extends BaseEntity, D> {

    D toDTO(E entity);
}
