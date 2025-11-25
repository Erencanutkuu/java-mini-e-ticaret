package com.example.minieticaret.catalog.mapper;

import com.example.minieticaret.catalog.domain.Category;
import com.example.minieticaret.catalog.dto.CategoryRequest;
import com.example.minieticaret.catalog.dto.CategoryResponse;
import com.example.minieticaret.common.mapper.MapStructConfig;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(config = MapStructConfig.class)
public interface CategoryMapper {

    @Mapping(source = "parent.id", target = "parentId")
    CategoryResponse toResponse(Category category);

    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "children", ignore = true)
    Category toEntity(CategoryRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "children", ignore = true)
    void updateEntity(CategoryRequest request, @MappingTarget Category category);
}
