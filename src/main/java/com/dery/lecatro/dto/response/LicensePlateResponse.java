package com.dery.lecatro.dto.response;

import com.dery.lecatro.entity.enums.LicensePlateStatus;
import java.time.LocalDate;
import java.util.UUID;

public record LicensePlateResponse(UUID publicId, String number, LocalDate issueDate, LicensePlateStatus status,
		RequestResponse request) {
}