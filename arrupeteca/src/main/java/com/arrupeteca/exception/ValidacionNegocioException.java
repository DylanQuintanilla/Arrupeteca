package com.arrupeteca.exception;

import lombok.Getter;
import java.util.List;

@Getter
public class ValidacionNegocioException extends RuntimeException {

    private final List<String> errores;

    public ValidacionNegocioException(List<String> errores) {
        super("Se encontraron múltiples errores de validación");
        this.errores = errores;
    }

    public ValidacionNegocioException(String mensaje) {
        super(mensaje);
        this.errores = List.of(mensaje);
    }
}