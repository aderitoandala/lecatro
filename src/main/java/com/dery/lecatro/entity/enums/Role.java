package com.dery.lecatro.entity.enums;

public enum Role {
	ADMIN("Administrador"), OPERATOR("Operador");

	private final String label;

	Role(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
}