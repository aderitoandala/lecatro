package com.dery.lecatro.entity.enums;

public enum PaymentStatus {
	PENDING("Pendente"), CONFIRMED("Confirmado"), REJECTED("Rejeitado");

	private final String label;

	PaymentStatus(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
}