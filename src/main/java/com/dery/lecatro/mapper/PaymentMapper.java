package com.dery.lecatro.mapper;

import com.dery.lecatro.dto.response.PaymentResponse;
import com.dery.lecatro.entity.Payment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

	PaymentResponse toResponse(Payment payment);
}