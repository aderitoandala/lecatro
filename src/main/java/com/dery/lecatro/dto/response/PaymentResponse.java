package com.dery.lecatro.dto.response;

import com.dery.lecatro.entity.enums.PaymentMethod;
import com.dery.lecatro.entity.enums.PaymentStatus;
import java.math.BigDecimal;
import java.util.UUID;

public record PaymentResponse(UUID publicId, UUID requestPublicId, BigDecimal amount, PaymentMethod method,
		PaymentStatus status) {
}