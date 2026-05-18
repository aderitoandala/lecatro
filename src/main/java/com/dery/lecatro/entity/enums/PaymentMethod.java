package com.dery.lecatro.entity.enums;

public enum PaymentMethod {	
	BANK_TRANSFER("Transferência Bancária"), MPESA("M-Pesa"), EMOLA("e-Mola");

	private final String label;

	PaymentMethod(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
}
