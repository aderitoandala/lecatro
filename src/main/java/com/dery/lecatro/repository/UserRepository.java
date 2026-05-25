package com.dery.lecatro.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dery.lecatro.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByEmail(String email);

	Optional<User> findByPublicId(UUID publicId);

	@Query("SELECT u FROM User u WHERE " + "LOWER(u.email) LIKE %:search% OR " + "LOWER(u.province) LIKE %:search% OR "
			+ "LOWER(u.role) LIKE %:search ")
	List<User> findBySearch(@Param("search") String search);

}