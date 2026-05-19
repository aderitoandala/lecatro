package com.dery.lecatro.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.dery.lecatro.dto.request.RequestRequest;
import com.dery.lecatro.dto.response.RequestResponse;
import com.dery.lecatro.entity.enums.RequestStatus;
import com.dery.lecatro.exception.ResourceNotFoundException;
import com.dery.lecatro.service.LicensePlateService;
import com.dery.lecatro.service.OwnerService;
import com.dery.lecatro.service.PaymentService;
import com.dery.lecatro.service.RequestService;
import com.dery.lecatro.service.VehicleService;
import com.dery.lecatro.util.PdfGenerator;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/requests")
@RequiredArgsConstructor
public class RequestController {

	private final RequestService requestService;
	private final OwnerService ownerService;
	private final VehicleService vehicleService;
	private final PaymentService paymentService;
	private final PdfGenerator pdfGenerator;
	private final LicensePlateService licensePlateService;

	@GetMapping
	public String list(@RequestParam(required = false) Integer year, @RequestParam(required = false) Integer month,
			@RequestParam(required = false) RequestStatus status, Model model) {
		model.addAttribute("requests", requestService.findWithFilters(year, month, status));

		int selectedYear = year != null ? year : LocalDate.now().getYear();
		model.addAttribute("stats", requestService.getStatsByYear(selectedYear));
		model.addAttribute("selectedYear", selectedYear);
		model.addAttribute("selectedMonth", month);
		model.addAttribute("selectedStatus", status);

		List<Integer> years = IntStream.rangeClosed(LocalDate.now().getYear() - 4, LocalDate.now().getYear()).boxed()
				.sorted(Comparator.reverseOrder()).toList();
		model.addAttribute("years", years);

		return "request/list";
	}

	@GetMapping("/new")
	public String createForm(Model model) {
		model.addAttribute("requestRequest", new RequestRequest(null, null));
		model.addAttribute("owners", ownerService.findAll());
		model.addAttribute("vehicles", vehicleService.findAll());
		return "request/form";
	}

	@PostMapping
	public String create(@Valid @ModelAttribute RequestRequest requestRequest, BindingResult result, Model model,
			RedirectAttributes redirectAttributes) {
		if (result.hasErrors()) {

			model.addAttribute("owners", ownerService.findAll());
			model.addAttribute("vehicles", vehicleService.findAll());
			return "request/form";
		}

		requestService.create(requestRequest);
		redirectAttributes.addFlashAttribute("mensagem", "Pedido criado com sucesso");
		return "redirect:/requests";
	}

	@GetMapping("/{publicId}")
	public String detail(@PathVariable UUID publicId, Model model) {
		RequestResponse request = requestService.findByPublicId(publicId);
		model.addAttribute("request", request);

		// carrega o pagamento associado se existir
		try {
			model.addAttribute("payment", paymentService.findByRequest(publicId));
		} catch (ResourceNotFoundException e) {
			model.addAttribute("payment", null); // sem pagamento ainda
		}

		// carrega a matrícula associada se existir (ISSUED)
		try {
			model.addAttribute("licensePlate", licensePlateService.findByRequestPublicId(publicId));
		} catch (ResourceNotFoundException e) {
			model.addAttribute("licensePlate", null);
		}

		return "request/detail";
	}

	@PostMapping("/{publicId}/cancel")
	public String cancel(@PathVariable UUID publicId, RedirectAttributes redirectAttributes) {
		requestService.cancel(publicId);
		redirectAttributes.addFlashAttribute("mensagem", "Pedido cancelado com sucesso");
		return "redirect:/requests";
	}

	@GetMapping("/pdf")
	public void exportPdf(@RequestParam(required = false) Integer year, @RequestParam(required = false) Integer month,
			@RequestParam(required = false) RequestStatus status, HttpServletResponse response) throws Exception {
		response.setContentType("application/pdf");
		response.setHeader("Content-Disposition", "attachment; filename=pedidos.pdf");

		// respeita os filtros activos
		List<String[]> rows = requestService.findWithFilters(year, month, status).stream()
				.map(r -> new String[] { r.owner().firstName() + " " + r.owner().lastName(),
						r.vehicle().brand() + " " + r.vehicle().model(), r.user().email(), r.status().getLabel(),
						r.createdAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) })
				.toList();

		// descrição dos filtros activos para o PDF
		String filterInfo = buildFilterInfo(year, month, status);

		pdfGenerator.generateRequests(rows, filterInfo, response.getOutputStream());
	}

	// constrói a descrição dos filtros para o cabeçalho do PDF
	private String buildFilterInfo(Integer year, Integer month, RequestStatus status) {
		List<String> parts = new ArrayList<>();
		if (year != null)
			parts.add("Ano: " + year);
		if (month != null)
			parts.add("Mês: " + month);
		if (status != null)
			parts.add("Estado: " + status.getLabel());
		return String.join(" | ", parts);
	}
}