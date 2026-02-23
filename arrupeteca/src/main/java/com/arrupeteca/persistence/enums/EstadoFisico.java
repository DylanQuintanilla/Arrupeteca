package com.arrupeteca.persistence.enums;

import lombok.Getter;

@Getter
public enum EstadoFisico {

    NUEVO("Nuevo"),
    COMO_NUEVO("Como nuevo"),
    BUEN_ESTADO("Buen estado"),
    ACEPTABLE("Aceptable"),
    DETERIORADO("Deteriorado"),
    MUY_DETERIORADO("Muy deteriorado"),
    INCOMPLETO("Incompleto");

    private final String nombre;

    EstadoFisico(String nombre) {
        this.nombre = nombre;
    }
}
