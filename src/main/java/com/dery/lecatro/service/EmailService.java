package com.dery.lecatro.service;

import com.dery.lecatro.entity.enums.RequestStatus;

public interface EmailService {

	// notifica o proprietário sobre a mudança de estado do pedido
	// chamado internamente após cada transição de estado — nunca pelo controller
	void sendRequestStatusNotification(String toEmail, // email do proprietário
			String ownerName, // nome do proprietário para personalização
			String requestId, // publicId do pedido em formato string
			RequestStatus status // novo estado — determina o conteúdo do email
	);
}