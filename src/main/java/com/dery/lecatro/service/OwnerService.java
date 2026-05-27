package com.dery.lecatro.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.dery.lecatro.dto.request.OwnerRequest;
import com.dery.lecatro.dto.response.OwnerResponse;

public interface OwnerService {

	OwnerResponse create(OwnerRequest request);

	OwnerResponse findByPublicId(UUID publicId);

	OwnerResponse update(UUID publicId, OwnerRequest request);

	void delete(UUID publicId);

	Page<OwnerResponse> findAll(Pageable pageable);

	Page<OwnerResponse> findBySearch(String search, Pageable pageable);
}