package com.dery.lecatro.entity.enums;

public enum Province {

    MAPUTO_CITY("MC"),   
    MAPUTO_PROVINCE("MP"),      
    GAZA("GZ"),        
    INHAMBANE("IB"),    
    SOFALA("SF"),     
    MANICA("MN"),        
    TETE("TT"),          
    ZAMBEZIA("ZB"),     
    NAMPULA("NP"),       
    CABO_DELGADO("CA"),  
    NIASSA("NS");      

    private final String code;

    Province(String code) {
        this.code = code;
    }

    public String getCode() {
        return code; 
    }
}