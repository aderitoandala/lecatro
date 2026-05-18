package com.dery.lecatro.entity.enums;

public enum RequestStatus {
	PENDING("Pendente"), PAID("Pago"), ISSUED("Emitido"), CANCELLED("Cancelado");

	private final String label;

	RequestStatus(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
}
