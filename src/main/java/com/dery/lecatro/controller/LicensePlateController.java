package com.dery.lecatro.controller;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.dery.lecatro.dto.response.LicensePlateResponse;
import com.dery.lecatro.entity.enums.LicensePlateStatus;
import com.dery.lecatro.exception.ResourceNotFoundException;
import com.dery.lecatro.service.LicensePlateService;
import com.dery.lecatro.util.PdfGenerator;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/license-plates")
@RequiredArgsConstructor
public class LicensePlateController {

	private final LicensePlateService licensePlateService;
	private final PdfGenerator pdfGenerator;

	@PostMapping("/issue/{requestPublicId}")
	public String issue(@PathVariable UUID requestPublicId, RedirectAttributes redirectAttributes) {

		licensePlateService.issue(requestPublicId);
		redirectAttributes.addFlashAttribute("mensagem", "Matrícula emitida com sucesso");
		return "redirect:/requests";
	}

	@GetMapping("/search")
	public String searchForm(Model model) {

		model.addAttribute("plates", licensePlateService.findByStatus(LicensePlateStatus.ACTIVE));

		return "license-plate/search";
	}

	@GetMapping("/search/result")
	public String searchResult(@RequestParam String number, Model model) {
		try {
			model.addAttribute("licensePlate", licensePlateService.findByNumber(number));
		} catch (ResourceNotFoundException e) {

			model.addAttribute("licensePlate", null);
		}
		return "license-plate/search";
	}

	@PostMapping("/{publicId}/cancel")
	public String cancel(@PathVariable UUID publicId, RedirectAttributes redirectAttributes) {
		licensePlateService.cancel(publicId);
		redirectAttributes.addFlashAttribute("mensagem", "Matrícula cancelada com sucesso");
		return "redirect:/requests";
	}

	@GetMapping("/pdf")
	public void exportPdf(HttpServletResponse response) throws Exception {
		response.setContentType("application/pdf");
		response.setHeader("Content-Disposition", "attachment; filename=matriculas.pdf");

		List<String[]> rows = licensePlateService.findByStatus(LicensePlateStatus.ACTIVE).stream()
				.map(p -> new String[] { p.number(),
						p.request().owner().firstName() + " " + p.request().owner().lastName(),
						p.request().vehicle().brand() + " " + p.request().vehicle().model(),
						p.issueDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), p.status().getLabel() })
				.toList();

		pdfGenerator.generateLicensePlates(rows, response.getOutputStream());
	}

	// certificado individual
	@GetMapping("/{publicId}/certificate")
	public void certificate(@PathVariable UUID publicId, HttpServletResponse response) throws Exception {
		response.setContentType("application/pdf");
		response.setHeader("Content-Disposition", "inline; filename=certificado-matricula.pdf"); // abre no browser

		LicensePlateResponse plate = licensePlateService.findByPublicId(publicId);
		DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

		pdfGenerator.generateCertificate(plate.number(), plate.issueDate().format(fmt),
				plate.request().owner().firstName() + " " + plate.request().owner().lastName(),
				plate.request().owner().nuit(), plate.request().vehicle().brand(), plate.request().vehicle().model(),
				plate.request().vehicle().chassisNumber(), String.valueOf(plate.request().vehicle().manufactureYear()),
				response.getOutputStream());
	}
	

}