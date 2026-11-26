package com.arrupeteca.exception;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.arrupeteca.dto.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Map<String, String> CONSTRAINTS_MAP = new HashMap<>();

    static {
        // Autores
        CONSTRAINTS_MAP.put("uk_autor_seudonimo", "El seudónimo ya está en uso.");
    }

    /**
     * MANEJO DE CREDENCIALES INVÁLIDAS (Login fallido).
     */
    @ExceptionHandler(org.springframework.security.authentication.BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadCredentials(
            org.springframework.security.authentication.BadCredentialsException ex) {
        log.warn("Intento de autenticación fallido: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("Credenciales inválidas. Verifique su correo y contraseña."));
    }

    /**
     * MANEJO DE ACCESO DENEGADO (Sin permisos suficientes).
     */
    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDenied(
            org.springframework.security.access.AccessDeniedException ex) {
        log.warn("Acceso denegado: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error("No tiene permisos suficientes para acceder a este recurso."));
    }

    /**
     * 1. MANEJO DE NUESTRAS EXCEPCIONES MANUALES (Reglas de negocio).
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Void>> handleRuntimeException(RuntimeException ex) {

        log.warn("Excepción de Runtime (Regla de negocio o fallo menor): {}", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ex.getMessage()));
    }

    /**
     * 2. MANEJO DE ERRORES DE VALIDACIÓN (Los @NotNull, @Size, @NotBlank de tus DTOs).
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> erroresDeCampos = new HashMap<>();

        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            erroresDeCampos.put(error.getField(), error.getDefaultMessage());
        }

        log.warn("Petición rechazada por DTO inválido. Campos afectados: {}", erroresDeCampos);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Existen errores de validación en los datos enviados", erroresDeCampos));
    }

    /**
     * 3. MANEJO DE ERRORES INESPERADOS (Caídas del sistema, NullPointers, etc.).
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGlobalException(Exception ex) {

        log.error("Error interno grave no controlado en el servidor: ", ex);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR) // Código 500
                .body(ApiResponse.error("Ocurrió un error interno en el servidor. "
                        + "Por favor, contacte al administrador."));
    }

    /**
     * 4. MANEJO DE VIOLACIÓN DE INTEGRIDAD DE DATOS (Duplicados, Llaves foráneas).
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {

        String mensajeRoot = ex.getRootCause() != null ? ex.getRootCause().getMessage() : ex.getMessage();
        String mensajeCliente = "Error de integridad de datos (duplicidad o restricción violada).";

        if (mensajeRoot != null) {
            for (Map.Entry<String, String> entry : CONSTRAINTS_MAP.entrySet()) {
                if (mensajeRoot.contains(entry.getKey())) {
                    mensajeCliente = entry.getValue();
                    break;
                }
            }
        }

        log.warn("Violación de integridad en BD. Mensaje devuelto al cliente: '{}' | Causa real en BD: {}",
                mensajeCliente, mensajeRoot);

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(mensajeCliente));
    }

    /**
     * 5. MANEJO DE ERRORES DE NEGOCIO ACUMULADOS.
     */
    @ExceptionHandler(ValidacionNegocioException.class)
    public ResponseEntity<ApiResponse<List<String>>> handleValidacionNegocio(ValidacionNegocioException ex) {

        log.warn("Reglas de negocio no cumplidas. Errores: {}", ex.getErrores());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("No se pudo procesar la solicitud debido a errores en los datos.",
                        ex.getErrores()));
    }

    /**
     * 6. MANEJO DE ERRORES DE LECTURA DE JSON (Formatos inválidos).
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {

        log.warn("Petición malformada: {}", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Error al procesar el cuerpo de la petición. Formato inválido."));
    }

    /**
     * 6b. MANEJO DE PARÁMETROS OBLIGATORIOS FALTANTES EN LA URL (?param=...).
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex) {

        log.warn("Parámetro de petición obligatorio faltante: {}", ex.getParameterName());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("El parámetro obligatorio '" + ex.getParameterName() + "' no fue proporcionado."));
    }

    /**
     * 7. MANEJO DE CONFLICTOS DE CONCURRENCIA (Bloqueo Optimista).
     */
    @ExceptionHandler(org.springframework.orm.ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<ApiResponse<Void>> handleOptimisticLockingFailure(
            org.springframework.orm.ObjectOptimisticLockingFailureException ex) {

        log.warn("Conflicto de edición detectado (Bloqueo Optimista): Alguien más actualizó el registro.");

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ApiResponse.error("El registro fue actualizado por otro usuario mientras lo editabas. "
                        + "Por favor, refresca los datos e intenta de nuevo."));
    }

}
