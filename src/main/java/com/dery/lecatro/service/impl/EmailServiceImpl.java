package com.dery.lecatro.service.impl;

import com.dery.lecatro.entity.enums.RequestStatus;
import com.dery.lecatro.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

	private final JavaMailSender mailSender; // injectado automaticamente pelo Spring Boot

	@Override
	@Async // envia o email numa thread separada — o utilizador não espera pelo envio
	public void sendRequestStatusNotification(String toEmail, String ownerName, String requestId,
			RequestStatus status) {
		try {
			SimpleMailMessage message = new SimpleMailMessage();

			message.setFrom("dery.lecatro@gmail.com"); // remetente fixo
			message.setTo(toEmail); // destinatário — email do proprietário
			message.setSubject(buildSubject(status)); // assunto dinâmico por estado
			message.setText(buildBody(ownerName, requestId, status)); // corpo dinâmico

			mailSender.send(message);

			// regista sucesso no log para rastreabilidade
			log.info("Email enviado para {} — pedido {} — estado {}", toEmail, requestId, status);

		} catch (MailException e) {
		
			log.error("Falha ao enviar email para {} — pedido {} — erro: {}", toEmail, requestId, e.getMessage());
		}
	}

	// assunto do email por estado do pedido
	private String buildSubject(RequestStatus status) {
		return switch (status) {
		case PENDING -> "LECATRO — Pedido de Matrícula Recebido";
		case PAID -> "LECATRO — Pagamento Confirmado";
		case ISSUED -> "LECATRO — Matrícula Emitida";
		case CANCELLED -> "LECATRO — Pedido Cancelado";
		};
	}

	// corpo do email por estado do pedido
	private String buildBody(String ownerName, String requestId, RequestStatus status) {
		String greeting = "Prezado(a) " + ownerName + ",\n\n";
		String footer = "\n\nAtenciosamente,\nEquipa LECATRO";

		String body = switch (status) {
		case PENDING -> "O seu pedido de matrícula foi registado com sucesso no sistema LECATRO.\n"
				+ "Referência do pedido: " + requestId + "\n\n" + "Aguarde a confirmação do pagamento para prosseguir.";

		case PAID -> "O pagamento referente ao pedido " + requestId + " foi confirmado.\n"
				+ "A emissão da sua matrícula será processada brevemente.";

		case ISSUED -> "A sua matrícula foi emitida com sucesso.\n" + "Referência do pedido: " + requestId + "\n\n"
				+ "Dirija-se ao balcão para levantamento do documento.";

		case CANCELLED -> "O pedido de matrícula com referência " + requestId + " foi cancelado.\n"
				+ "Para mais informações, contacte o nosso serviço de apoio.";
		};

		return greeting + body + footer;
	}
}