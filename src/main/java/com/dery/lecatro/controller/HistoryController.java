package com.dery.lecatro.controller;

import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.dery.lecatro.service.HistoryService;
import com.dery.lecatro.service.RequestService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/history")
@RequiredArgsConstructor
public class HistoryController {

	private final HistoryService historyService;
	private final RequestService requestService;

	@GetMapping("/{requestPublicId}")
	public String list(@PathVariable UUID requestPublicId, Model model) {
		model.addAttribute("history", historyService.findByRequest(requestPublicId));
		model.addAttribute("request", requestService.findByPublicId(requestPublicId));
		return "history/list";
	}
}