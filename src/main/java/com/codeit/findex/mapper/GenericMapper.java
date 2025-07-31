package com.codeit.findex.mapper;

import java.util.List;

public interface GenericMapper<D,E> {
    D toDto(E entity);
    E toEntity(D dto);

    List<D> toDtos(List<E> entities);
    List<E> toEntities(List<D> dtos);

}