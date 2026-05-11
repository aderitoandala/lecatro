package com.dery.lecatro.service;

import com.dery.lecatro.dto.request.OwnerRequest;
import com.dery.lecatro.dto.response.OwnerResponse;

import java.util.List;
import java.util.UUID;

public interface OwnerService {

	OwnerResponse create(OwnerRequest request);

	List<OwnerResponse> findAll();

	OwnerResponse findByPublicId(UUID publicId);

	OwnerResponse update(UUID publicId, OwnerRequest request);

	void delete(UUID publicId);
}