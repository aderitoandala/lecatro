package com.dery.lecatro.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.dery.lecatro.dto.response.PaymentResponse;
import com.dery.lecatro.entity.Payment;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

	@Mapping(source = "request.publicId", target = "requestPublicId")
	PaymentResponse toResponse(Payment payment);
}