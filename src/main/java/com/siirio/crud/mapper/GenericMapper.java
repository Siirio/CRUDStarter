package com.siirio.crud.mapper;

import org.mapstruct.MappingTarget;

public interface GenericMapper<T> {
    void updateFromDto(T source, @MappingTarget T target);
}
