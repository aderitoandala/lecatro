package com.dery.lecatro.dto.response;

import com.dery.lecatro.entity.enums.RequestStatus;
import java.time.LocalDateTime;
import java.util.UUID;

public record RequestResponse(UUID publicId, OwnerResponse owner, VehicleResponse vehicle, UserResponse user,
		RequestStatus status, LocalDateTime createdAt) {
}