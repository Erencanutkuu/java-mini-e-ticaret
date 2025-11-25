package com.example.minieticaret.customer.mapper;

import com.example.minieticaret.common.mapper.MapStructConfig;
import com.example.minieticaret.customer.domain.Address;
import com.example.minieticaret.customer.dto.AddressRequest;
import com.example.minieticaret.customer.dto.AddressResponse;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(config = MapStructConfig.class)
public interface AddressMapper {

    @Mapping(target = "user", ignore = true)
    Address toEntity(AddressRequest request);

    AddressResponse toResponse(Address address);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "user", ignore = true)
    void update(AddressRequest request, @MappingTarget Address address);
}
