package com.dery.lecatro.mapper;

import com.dery.lecatro.dto.request.OwnerRequest;
import com.dery.lecatro.dto.response.OwnerResponse;
import com.dery.lecatro.entity.Owner;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OwnerMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "publicId", ignore = true)
	Owner toEntity(OwnerRequest request);

	OwnerResponse toResponse(Owner owner);

}