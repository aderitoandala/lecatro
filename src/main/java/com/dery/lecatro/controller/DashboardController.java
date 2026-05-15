package com.dery.lecatro.controller;

import com.dery.lecatro.entity.enums.RequestStatus;
import com.dery.lecatro.entity.enums.LicensePlateStatus;
import com.dery.lecatro.service.LicensePlateService;
import com.dery.lecatro.service.OwnerService;
import com.dery.lecatro.service.RequestService;
import com.dery.lecatro.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class DashboardController {

	private final RequestService requestService;
	private final OwnerService ownerService;
	private final VehicleService vehicleService;
	private final LicensePlateService licensePlateService;

	@GetMapping("/dashboard")
	public String dashboard(Model model) {

		model.addAttribute("totalPending", requestService.findByStatus(RequestStatus.PENDING).size());

		model.addAttribute("totalIssued", licensePlateService.findByStatus(LicensePlateStatus.ACTIVE).size());

		model.addAttribute("totalOwners", ownerService.findAll().size());

		model.addAttribute("totalVehicles", vehicleService.findAll().size());

		return "dashboard";
	}
}