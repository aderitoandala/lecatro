package com.dery.lecatro.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.dery.lecatro.entity.enums.LicensePlateStatus;
import com.dery.lecatro.service.LicensePlateService;
import com.dery.lecatro.service.OwnerService;
import com.dery.lecatro.service.RequestService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class DashboardController {

	private final RequestService requestService;
	private final OwnerService ownerService;

	private final LicensePlateService licensePlateService;

	@GetMapping("/dashboard")
	public String dashboard(Model model, Authentication authentication) {

		model.addAttribute("userEmail", authentication.getName());

		model.addAttribute("today",
				LocalDate.now().format(DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy", Locale.of("pt", "MZ"))));

		model.addAttribute("totalToday", requestService.findToday().size());

		model.addAttribute("totalAwaiting", requestService.findAwaitingAction().size());

		model.addAttribute("totalPlates", licensePlateService.findByStatus(LicensePlateStatus.ACTIVE).size());

		model.addAttribute("totalOwners", ownerService.findAll().size());

		model.addAttribute("awaitingRequests", requestService.findAwaitingAction());

		model.addAttribute("recentPlates", licensePlateService.findRecent(5));
		
		model.addAttribute("greeting", buildGreeting());

		return "dashboard";
	}

	private String buildGreeting() {
		int hour = LocalDateTime.now().getHour();
		if (hour >= 0 && hour < 12)
			return "Bom dia";
		if (hour >= 12 && hour < 18)
			return "Boa tarde";
		return "Boa noite";
	}
}