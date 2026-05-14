package com.dery.lecatro.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dery.lecatro.dto.request.OwnerRequest;
import com.dery.lecatro.dto.response.OwnerResponse;
import com.dery.lecatro.entity.Owner;
import com.dery.lecatro.exception.DataIntegrityException;
import com.dery.lecatro.exception.ResourceNotFoundException;
import com.dery.lecatro.mapper.OwnerMapper;
import com.dery.lecatro.repository.OwnerRepository;
import com.dery.lecatro.service.OwnerService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OwnerServiceImpl implements OwnerService {

	private final OwnerRepository ownerRepository;
	private final OwnerMapper ownerMapper;

	@Override
	@Transactional
	public OwnerResponse create(OwnerRequest request) {

		if (ownerRepository.existsByNuit(request.nuit())) {
			throw new DataIntegrityException("Já existe um proprietário com este NUIT");
		}

		Owner owner = ownerMapper.toEntity(request);
		return ownerMapper.toResponse(ownerRepository.save(owner));
	}

	@Override
	@Transactional(readOnly = true)
	public List<OwnerResponse> findAll() {
		return ownerRepository.findAll().stream().map(ownerMapper::toResponse).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public OwnerResponse findByPublicId(UUID publicId) {
		return ownerRepository.findByPublicId(publicId).map(ownerMapper::toResponse)
				.orElseThrow(() -> new ResourceNotFoundException("Proprietário não encontrado"));
	}

	@Override
	@Transactional
	public OwnerResponse update(UUID publicId, OwnerRequest request) {
		Owner owner = ownerRepository.findByPublicId(publicId)
				.orElseThrow(() -> new ResourceNotFoundException("Proprietário não encontrado"));

		owner.setFirstName(request.firstName());
		owner.setLastName(request.lastName());
		owner.setNuit(request.nuit());
		owner.setEmail(request.email());
		owner.setBirthDate(request.birthDate());

		return ownerMapper.toResponse(ownerRepository.save(owner));
	}

	@Override
	@Transactional
	public void delete(UUID publicId) {
		Owner owner = ownerRepository.findByPublicId(publicId)
				.orElseThrow(() -> new ResourceNotFoundException("Proprietário não encontrado"));

		ownerRepository.delete(owner);
	}
}