package com.arrupeteca.persistence.enums;

import lombok.Getter;

@Getter
public enum Ciclo {

    PRIMER("Primer ciclo"),
    SEGUNDO("Segundo ciclo"),
    TERCER("Tercer ciclo"),
    CUARTO("Cuarto ciclo");

    private final String nombre;

    Ciclo(String nombre) {
        this.nombre = nombre;
    }
}
