package com.dery.lecatro.mapper;

import com.dery.lecatro.dto.request.VehicleRequest;
import com.dery.lecatro.dto.response.VehicleResponse;
import com.dery.lecatro.entity.Vehicle;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface VehicleMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "publicId", ignore = true)
	Vehicle toEntity(VehicleRequest request);

	VehicleResponse toResponse(Vehicle vehicle);

}