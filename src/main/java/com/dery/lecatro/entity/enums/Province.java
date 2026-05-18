package com.dery.lecatro.entity.enums;

public enum Province {

    MAPUTO_CITY("MC", "Maputo Cidade"),
    MAPUTO("MP", "Maputo Província"),
    GAZA("GZ", "Gaza"),
    INHAMBANE("IB", "Inhambane"),
    SOFALA("SF", "Sofala"),
    MANICA("MN", "Manica"),
    TETE("TT", "Tete"),
    ZAMBEZIA("ZB", "Zambézia"),
    NAMPULA("NP", "Nampula"),
    CABO_DELGADO("CA", "Cabo Delgado"),
    NIASSA("NS", "Niassa");

    private final String code;
    private final String label; 

    Province(String code, String label) {
        this.code = code;
        this.label = label;
    }

    public String getCode() {
    	return code; 
    }

	public String getLabel() {
		return label;
	}
}