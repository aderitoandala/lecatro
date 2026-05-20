package com.dery.lecatro.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.dery.lecatro.dto.request.VehicleRequest;
import com.dery.lecatro.dto.response.VehicleResponse;
import com.dery.lecatro.exception.DataIntegrityException;
import com.dery.lecatro.service.VehicleService;
import com.dery.lecatro.util.PdfGenerator;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/vehicles")
@RequiredArgsConstructor
public class VehicleController {

	private final VehicleService vehicleService;
	private final PdfGenerator pdfGenerator;

	@GetMapping
	public String list(Model model) {
		model.addAttribute("vehicles", vehicleService.findAll());
		return "vehicle/list";
	}

	@GetMapping("/new")
	public String createForm(Model model) {
		model.addAttribute("form", new VehicleRequest(null, null, null, null, null));
		return "vehicle/form";
	}

	@PostMapping
	public String create(@Valid @ModelAttribute VehicleRequest form, BindingResult result,
			RedirectAttributes redirectAttributes) {
		if (result.hasErrors())
			return "vehicle/form";

		vehicleService.create(form);
		redirectAttributes.addFlashAttribute("mensagem", "Veículo criado com sucesso");
		return "redirect:/vehicles";
	}

	@GetMapping("/{publicId}/edit")
	public String editForm(@PathVariable UUID publicId, Model model) {

		VehicleResponse vehicle = vehicleService.findByPublicId(publicId);
		model.addAttribute("form", new VehicleRequest(vehicle.brand(), vehicle.model(), vehicle.color(),
				vehicle.chassisNumber(), vehicle.manufactureYear()));
		model.addAttribute("publicId", publicId);
		model.addAttribute("editMode", true);
		return "vehicle/form";
	}

	@PostMapping("/{publicId}/edit")
	public String update(@PathVariable UUID publicId, @Valid @ModelAttribute VehicleRequest form, BindingResult result,
			RedirectAttributes redirectAttributes) {
		if (result.hasErrors())
			return "vehicle/form";

		try {

			vehicleService.update(publicId, form);
			redirectAttributes.addFlashAttribute("mensagem", "Veículo actualizado com sucesso");
			return "redirect:/vehicles";
		} catch (DataIntegrityException e) {
			redirectAttributes.addFlashAttribute("erro", e.getMessage());
			return "redirect:/vehicles/" + publicId + "/edit";
		}
	}

	@PostMapping("/{publicId}/delete")
	public String delete(@PathVariable UUID publicId, RedirectAttributes redirectAttributes) {
		try {
			vehicleService.delete(publicId);
			redirectAttributes.addFlashAttribute("mensagem", "Veículo removido com sucesso");
		} catch (org.springframework.dao.DataIntegrityViolationException e) {
			redirectAttributes.addFlashAttribute("erro",
					"Este veículo não pode ser eliminado porque tem pedidos associados.");
		}
		return "redirect:/vehicles";
	}

	@GetMapping("/pdf")
	public void exportPdf(HttpServletResponse response) throws Exception {
		response.setContentType("application/pdf");
		response.setHeader("Content-Disposition", "attachment; filename=veiculos.pdf");

		List<String[]> rows = vehicleService.findAll().stream().map(v -> new String[] { v.brand(), v.model(), v.color(),
				v.chassisNumber(), String.valueOf(v.manufactureYear()) }).toList();

		pdfGenerator.generateVehicles(rows, response.getOutputStream());
	}
}