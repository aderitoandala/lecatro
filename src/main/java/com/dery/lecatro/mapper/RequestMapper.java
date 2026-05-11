package com.dery.lecatro.mapper;

import com.dery.lecatro.dto.response.RequestResponse;
import com.dery.lecatro.entity.Request;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = { OwnerMapper.class, VehicleMapper.class, UserMapper.class })
public interface RequestMapper {

	RequestResponse toResponse(Request request);
}