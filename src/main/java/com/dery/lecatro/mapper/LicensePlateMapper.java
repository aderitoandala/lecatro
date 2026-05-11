package com.dery.lecatro.mapper;

import com.dery.lecatro.dto.response.LicensePlateResponse;
import com.dery.lecatro.entity.LicensePlate;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = { RequestMapper.class })
public interface LicensePlateMapper {

	LicensePlateResponse toResponse(LicensePlate licensePlate);
}