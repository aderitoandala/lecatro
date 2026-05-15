package com.dery.lecatro.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.dery.lecatro.dto.request.UserRequest;
import com.dery.lecatro.dto.response.UserResponse;
import com.dery.lecatro.entity.User;
import com.dery.lecatro.entity.enums.Province;
import com.dery.lecatro.entity.enums.Role;
import com.dery.lecatro.exception.ResourceNotFoundException;
import com.dery.lecatro.mapper.UserMapper;
import com.dery.lecatro.repository.UserRepository;
import com.dery.lecatro.service.impl.UserServiceImpl;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private UserMapper userMapper;

	@Mock
	private PasswordEncoder passwordEncoder; 

	@InjectMocks
	private UserServiceImpl userService;

	private User user;
	private UserRequest userRequest;
	private UserResponse userResponse;
	private UUID publicId;

	@BeforeEach
	void setUp() {
		publicId = UUID.randomUUID();

		user = User.builder().id(1L).publicId(publicId).email("admin@lecatro.mz").password("encoded_password")
				.province(Province.MAPUTO_CITY).role(Role.ADMIN).build();

		userRequest = new UserRequest("admin@lecatro.mz", "senha123", Province.MAPUTO_CITY, Role.ADMIN);

		userResponse = new UserResponse(publicId, "admin@lecatro.mz", Province.MAPUTO_CITY, Role.ADMIN);
	}

	@Test
	void shouldCreateUserSuccessfully() {
		// Arrange
		when(userMapper.toEntity(userRequest)).thenReturn(user);
		when(passwordEncoder.encode(userRequest.password())).thenReturn("encoded_password");
		when(userRepository.save(user)).thenReturn(user);
		when(userMapper.toResponse(user)).thenReturn(userResponse);

		// Act
		UserResponse result = userService.create(userRequest);

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.email()).isEqualTo("admin@lecatro.mz");
		// verifica que a senha foi codificada antes de guardar
		verify(passwordEncoder, times(1)).encode("senha123");
		verify(userRepository, times(1)).save(user);
	}

	@Test
	void shouldEncodePasswordBeforeSaving() {
		// Arrange
		when(userMapper.toEntity(userRequest)).thenReturn(user);
		when(passwordEncoder.encode(anyString())).thenReturn("encoded_password");
		when(userRepository.save(any(User.class))).thenReturn(user);
		when(userMapper.toResponse(user)).thenReturn(userResponse);

		// Act
		userService.create(userRequest);

		// Assert
		verify(passwordEncoder, times(1)).encode(userRequest.password());
		assertThat(user.getPassword()).isEqualTo("encoded_password");
	}

	@Test
	void shouldListAllUsers() {
		// Arrange
		when(userRepository.findAll()).thenReturn(List.of(user));
		when(userMapper.toResponse(user)).thenReturn(userResponse);

		// Act
		List<UserResponse> result = userService.findAll();

		// Assert
		assertThat(result).hasSize(1);
		assertThat(result.get(0).email()).isEqualTo("admin@lecatro.mz");
	}

	@Test
	void shouldFindUserByPublicId() {
		// Arrange
		when(userRepository.findByPublicId(publicId)).thenReturn(Optional.of(user));
		when(userMapper.toResponse(user)).thenReturn(userResponse);

		// Act
		UserResponse result = userService.findByPublicId(publicId);

		// Assert
		assertThat(result.publicId()).isEqualTo(publicId);
	}

	@Test
	void shouldThrowExceptionWhenUserNotFound() {
		// Arrange
		when(userRepository.findByPublicId(publicId)).thenReturn(Optional.empty());

		// Act + Assert
		assertThrows(ResourceNotFoundException.class, () -> userService.findByPublicId(publicId));
	}

	@Test
	void shouldDeleteUser() {
		// Arrange
		when(userRepository.findByPublicId(publicId)).thenReturn(Optional.of(user));

		// Act
		userService.delete(publicId);

		// Assert
		verify(userRepository, times(1)).delete(user);
	}
}