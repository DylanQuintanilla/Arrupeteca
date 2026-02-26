package com.arrupeteca.exception;

import com.arrupeteca.dto.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 1. MANEJO DE NUESTRAS EXCEPCIONES MANUALES (Reglas de negocio)
     * Atrapa todos los 'throw new RuntimeException("...");' que pusiste en tus Services.
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Void>> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ex.getMessage()));
    }

    /**
     * 2. MANEJO DE ERRORES DE VALIDACIÓN (Los @NotNull, @Size, @NotBlank de tus DTOs)
     * Atrapa los errores cuando el JSON que manda el Frontend no cumple las reglas.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> erroresDeCampos = new HashMap<>();

        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            erroresDeCampos.put(error.getField(), error.getDefaultMessage());
        }

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Existen errores de validación en los datos enviados", erroresDeCampos));
    }

    /**
     * 3. MANEJO DE ERRORES INESPERADOS (Caídas del sistema, NullPointers, etc.)
     * Es nuestra red de seguridad final. Evita que el cliente vea código Java extraño.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGlobalException(Exception ex) {
        // En un proyecto real aquí podrías poner un log.error(ex.getMessage(), ex); para ver el error en consola

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR) // Código 500
                .body(ApiResponse.error("Ocurrió un error interno en el servidor. Por favor, contacte al administrador."));
    }
}