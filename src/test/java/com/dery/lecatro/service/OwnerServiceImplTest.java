package com.dery.lecatro.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.dery.lecatro.dto.request.OwnerRequest;
import com.dery.lecatro.dto.response.OwnerResponse;
import com.dery.lecatro.entity.Owner;
import com.dery.lecatro.exception.DataIntegrityException;
import com.dery.lecatro.exception.ResourceNotFoundException;
import com.dery.lecatro.mapper.OwnerMapper;
import com.dery.lecatro.repository.OwnerRepository;
import com.dery.lecatro.service.impl.OwnerServiceImpl;

@ExtendWith(MockitoExtension.class)
class OwnerServiceImplTest {

	@Mock
	private OwnerRepository ownerRepository;

	@Mock
	private OwnerMapper ownerMapper;

	@InjectMocks
	private OwnerServiceImpl ownerService;

	private Owner owner;
	private OwnerRequest ownerRequest;
	private OwnerResponse ownerResponse;
	private UUID publicId;

	@BeforeEach
	void setUp() {
		publicId = UUID.randomUUID();

		owner = Owner.builder().id(1L).publicId(publicId).firstName("Aderito").lastName("Andala").nuit("123456789")
				.email("dery@email.com").birthDate(LocalDate.of(2006, 07, 28)).build();

		ownerRequest = new OwnerRequest("Aderito", "Andala", "123456789", "dery@email.com", LocalDate.of(2006, 07, 28));

		ownerResponse = new OwnerResponse(publicId, "Aderito", "Andala", "123456789", "dery@email.com",
				LocalDate.of(2006, 07, 28));
	}

	@Test
	void shouldCreateOwnerSuccessfully() {
		// Arrange
		when(ownerRepository.existsByNuit(ownerRequest.nuit())).thenReturn(false);
		when(ownerMapper.toEntity(ownerRequest)).thenReturn(owner);
		when(ownerRepository.save(owner)).thenReturn(owner);
		when(ownerMapper.toResponse(owner)).thenReturn(ownerResponse);

		// Act
		OwnerResponse result = ownerService.create(ownerRequest);

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.nuit()).isEqualTo("123456789");
		verify(ownerRepository, times(1)).save(owner);
	}

	@Test
	void shouldThrowExceptionWhenNuitAlreadyExists() {
		// Arrange
		when(ownerRepository.existsByNuit(ownerRequest.nuit())).thenReturn(true);

		// Act + Assert
		assertThrows(DataIntegrityException.class, () -> ownerService.create(ownerRequest));
		verify(ownerRepository, never()).save(any());
	}

	@Test
	void shouldListAllOwners() {
	    // Arrange
	   
	    Page<Owner> ownerPage = new PageImpl<>(List.of(owner));
	    
		when(ownerRepository.findAll(any(Pageable.class))).thenReturn(ownerPage);
	    
	    when(ownerMapper.toResponse(owner)).thenReturn(ownerResponse);

	    // Act
	    List<OwnerResponse> result = ownerService.findAll(Pageable.unpaged()).getContent();

	    // Assert
	    assertThat(result).hasSize(1);
	    assertThat(result.get(0).nuit()).isEqualTo("123456789");
	}

	@Test
	void shouldFindOwnerByPublicId() {
		// Arrange
		when(ownerRepository.findByPublicId(publicId)).thenReturn(Optional.of(owner));
		when(ownerMapper.toResponse(owner)).thenReturn(ownerResponse);

		// Act
		OwnerResponse result = ownerService.findByPublicId(publicId);

		// Assert
		assertThat(result.publicId()).isEqualTo(publicId);
	}

	@Test
	void shouldThrowExceptionWhenOwnerNotFound() {
		// Arrange
		when(ownerRepository.findByPublicId(publicId)).thenReturn(Optional.empty());

		// Act + Assert
		assertThrows(ResourceNotFoundException.class, () -> ownerService.findByPublicId(publicId));
	}

	@Test
	void shouldDeleteOwner() {
		// Arrange
		when(ownerRepository.findByPublicId(publicId)).thenReturn(Optional.of(owner));

		// Act
		ownerService.delete(publicId);

		// Assert
		verify(ownerRepository, times(1)).delete(owner);
	}
}