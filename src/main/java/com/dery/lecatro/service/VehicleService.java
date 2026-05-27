package com.dery.lecatro.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.dery.lecatro.dto.request.VehicleRequest;
import com.dery.lecatro.dto.response.VehicleResponse;

public interface VehicleService {

	VehicleResponse create(VehicleRequest request);

	VehicleResponse findByPublicId(UUID publicId);

	VehicleResponse update(UUID publicId, VehicleRequest request);

	void delete(UUID publicId);

	Page<VehicleResponse> findAll(Pageable pageable);

	Page<VehicleResponse> findBySearch(String search, Pageable pageable);
}