package com.dery.lecatro.service;

import java.util.List;
import java.util.UUID;

import com.dery.lecatro.dto.response.LicensePlateResponse;
import com.dery.lecatro.entity.enums.LicensePlateStatus;

public interface LicensePlateService {

	LicensePlateResponse issue(UUID requestPublicId);

	LicensePlateResponse findByNumber(String number);

	List<LicensePlateResponse> findByStatus(LicensePlateStatus status);

	LicensePlateResponse cancel(UUID publicId);

	List<LicensePlateResponse> findRecent(int limit);

	LicensePlateResponse findByPublicId(UUID publicId);

	LicensePlateResponse findByRequestPublicId(UUID requestPublicId);
}