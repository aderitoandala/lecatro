package com.dery.lecatro.entity.enums;

public enum HistoryEvent {

	REGISTRATION("Registo"), PAYMENT("Pagamento"), MODIFICATION("Alteração"), CANCELLATION("Cancelamento");

	private String label;

	private HistoryEvent(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

}
