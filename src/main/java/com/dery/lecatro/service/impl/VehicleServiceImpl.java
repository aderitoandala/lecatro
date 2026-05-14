package com.dery.lecatro.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dery.lecatro.dto.request.VehicleRequest;
import com.dery.lecatro.dto.response.VehicleResponse;
import com.dery.lecatro.entity.Vehicle;
import com.dery.lecatro.exception.DataIntegrityException;
import com.dery.lecatro.exception.ResourceNotFoundException;
import com.dery.lecatro.mapper.VehicleMapper;
import com.dery.lecatro.repository.VehicleRepository;
import com.dery.lecatro.service.VehicleService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {

	private final VehicleRepository vehicleRepository;
	private final VehicleMapper vehicleMapper;

	@Override
	@Transactional
	public VehicleResponse create(VehicleRequest request) {

		if (vehicleRepository.existsByChassisNumber(request.chassisNumber())) {
			throw new DataIntegrityException("Já existe um veículo com este número de chassis");
		}

		Vehicle vehicle = vehicleMapper.toEntity(request);
		return vehicleMapper.toResponse(vehicleRepository.save(vehicle));
	}

	@Override
	@Transactional(readOnly = true)
	public List<VehicleResponse> findAll() {
		return vehicleRepository.findAll().stream().map(vehicleMapper::toResponse).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public VehicleResponse findByPublicId(UUID publicId) {
		return vehicleRepository.findByPublicId(publicId).map(vehicleMapper::toResponse)
				.orElseThrow(() -> new ResourceNotFoundException("Veículo não encontrado"));
	}

	@Override
	@Transactional
	public VehicleResponse update(UUID publicId, VehicleRequest request) {
		Vehicle vehicle = vehicleRepository.findByPublicId(publicId)
				.orElseThrow(() -> new ResourceNotFoundException("Veículo não encontrado"));

		vehicle.setBrand(request.brand());
		vehicle.setModel(request.model());
		vehicle.setColor(request.color());
		vehicle.setChassisNumber(request.chassisNumber());
		vehicle.setManufactureYear(request.manufactureYear());

		return vehicleMapper.toResponse(vehicleRepository.save(vehicle));
	}

	@Override
	@Transactional
	public void delete(UUID publicId) {
		Vehicle vehicle = vehicleRepository.findByPublicId(publicId)
				.orElseThrow(() -> new ResourceNotFoundException("Veículo não encontrado"));

		vehicleRepository.delete(vehicle);
	}
}