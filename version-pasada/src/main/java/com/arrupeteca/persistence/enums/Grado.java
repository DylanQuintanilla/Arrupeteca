package com.arrupeteca.persistence.enums;

import lombok.Getter;

@Getter
public enum Grado {

    INICIAL3_TO_PARVULARIA("Inicial 3 - Parvularia"),
    PRIMER_TO_NOVENO("Primer Grado - Noveno Grado"),
    BACHI_GENERAL("Bachillerato General"),
    BACHI_TEC_CONTADOR("Bachillerato tecnico contador"),
    BACHI_TEC_ELECTRONICA("Bachillerato tecnico electronica"),
    BACHI_TEC_SOFTWARE("Bachillerato tecnico en software");

    private final String nombre;

    Grado(String nombre) {
        this.nombre = nombre;
    }

}
