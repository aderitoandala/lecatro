package com.dery.lecatro.entity.enums;

public enum LicensePlateStatus {
	ACTIVE("Activa"), CANCELLED("Cancelada");

	private final String label;

	LicensePlateStatus(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
}
