package com.dery.lecatro.dto.response;

import java.time.LocalDate;
import java.util.UUID;

public record OwnerResponse(UUID publicId, String firstName, String lastName, String nuit, String email,
		LocalDate birthDate) {
}