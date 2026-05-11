package com.dery.lecatro.dto.response;

import java.util.UUID;

public record VehicleResponse(UUID publicId, String brand, String model, String color, String chassisNumber,
		Integer manufactureYear) {
}