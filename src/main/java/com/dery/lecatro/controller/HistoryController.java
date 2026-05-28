package com.dery.lecatro.controller;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.dery.lecatro.dto.response.RequestResponse;
import com.dery.lecatro.service.HistoryService;
import com.dery.lecatro.service.RequestService;
import com.dery.lecatro.util.PdfGenerator;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/history")
@RequiredArgsConstructor
public class HistoryController {

	private final HistoryService historyService;
	private final RequestService requestService;
	private final PdfGenerator pdfGenerator;

	@GetMapping("/{requestPublicId}")
	public String list(@PathVariable UUID requestPublicId, Model model) {
		model.addAttribute("history", historyService.findByRequest(requestPublicId));
		model.addAttribute("request", requestService.findByPublicId(requestPublicId));
		return "history/list";
	}

	@GetMapping("/{requestPublicId}/pdf")
	public void exportPdf(@PathVariable UUID requestPublicId, HttpServletResponse response) throws Exception {
		response.setContentType("application/pdf");
		response.setHeader("Content-Disposition", "attachment; filename=historico.pdf");

		RequestResponse request = requestService.findByPublicId(requestPublicId);
		String requestInfo = request.owner().firstName() + " " + request.owner().lastName() + " -- "
				+ request.vehicle().brand() + " " + request.vehicle().model();

		List<String[]> rows = historyService.findByRequest(requestPublicId).stream()
				.map(h -> new String[] { h.event().name(), h.description(),
						h.occurredAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) })
				.toList();

		pdfGenerator.generateHistory(requestInfo, rows, response.getOutputStream());
	}
}