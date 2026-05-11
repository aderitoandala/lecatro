package com.dery.lecatro.service;

import com.dery.lecatro.dto.response.LicensePlateResponse;

import java.util.UUID;

public interface LicensePlateService {

	LicensePlateResponse issue(UUID requestPublicId);

	LicensePlateResponse findByNumber(String number);

	LicensePlateResponse cancel(UUID publicId);
}