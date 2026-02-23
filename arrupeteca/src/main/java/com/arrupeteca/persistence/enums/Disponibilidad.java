package com.arrupeteca.persistence.enums;

import lombok.Getter;

@Getter
public enum Disponibilidad {

    DISPONIBLE("Disponible"),
    PRESTADO("Prestado"),
    RESERVADO("Reservado"),
    MANTENIMIENTO("Mantenimiento"),
    PERDIDO("Perdido"),
    BOTADO("Botado"),
    DONADO("Donado");

    private final String nombre;

    Disponibilidad(String nombre){
        this.nombre = nombre;
    }
}
