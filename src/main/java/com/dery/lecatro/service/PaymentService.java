package com.dery.lecatro.service;

import com.dery.lecatro.dto.request.PaymentRequest;
import com.dery.lecatro.dto.response.PaymentResponse;

import java.util.UUID;

public interface PaymentService {

	PaymentResponse create(PaymentRequest request);

	PaymentResponse confirm(UUID publicId);

	PaymentResponse reject(UUID publicId);

	PaymentResponse findByRequest(UUID requestPublicId);
}