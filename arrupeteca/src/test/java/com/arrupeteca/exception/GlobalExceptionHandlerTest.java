package com.arrupeteca.exception;

import com.arrupeteca.dto.response.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void handleRuntimeExceptionShouldReturnBadRequest() {
        RuntimeException ex = new RuntimeException("Test runtime exception");
        ResponseEntity<ApiResponse<Void>> response = handler.handleRuntimeException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertFalse(response.getBody().isExito());
        assertEquals("Test runtime exception", response.getBody().getMensaje());
    }

    @Test
    void handleValidationExceptionsShouldReturnBadRequestWithFieldErrors() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("object", "field", "error message");

        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        ResponseEntity<ApiResponse<Map<String, String>>> response = handler.handleValidationExceptions(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertFalse(response.getBody().isExito());
        assertEquals("Existen errores de validación en los datos enviados", response.getBody().getMensaje());
        assertNotNull(response.getBody().getData());
        assertEquals("error message", response.getBody().getData().get("field"));
    }

    @Test
    void handleGlobalExceptionShouldReturnInternalServerError() {
        Exception ex = new Exception("Test exception");
        ResponseEntity<ApiResponse<Void>> response = handler.handleGlobalException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertFalse(response.getBody().isExito());
        assertEquals("Ocurrió un error interno en el servidor. Por favor, contacte al administrador.", response.getBody().getMensaje());
    }

    @Test
    void handleDataIntegrityViolationExceptionWithRootCauseAndMatchShouldReturnConflict() {
        DataIntegrityViolationException ex = mock(DataIntegrityViolationException.class);
        Throwable rootCause = mock(Throwable.class);
        when(rootCause.getMessage()).thenReturn("uk_autor_seudonimo violation");
        when(ex.getRootCause()).thenReturn(rootCause);

        ResponseEntity<ApiResponse<Void>> response = handler.handleDataIntegrityViolationException(ex);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertFalse(response.getBody().isExito());
        assertEquals("El seudónimo ya está en uso.", response.getBody().getMensaje());
    }

    @Test
    void handleDataIntegrityViolationExceptionWithRootCauseAndNoMatchShouldReturnConflictDefaultMessage() {
        DataIntegrityViolationException ex = mock(DataIntegrityViolationException.class);
        Throwable rootCause = mock(Throwable.class);
        when(rootCause.getMessage()).thenReturn("some other error");
        when(ex.getRootCause()).thenReturn(rootCause);

        ResponseEntity<ApiResponse<Void>> response = handler.handleDataIntegrityViolationException(ex);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertFalse(response.getBody().isExito());
        assertEquals("Error de integridad de datos (duplicidad o restricción violada).", response.getBody().getMensaje());
    }

    @Test
    void handleDataIntegrityViolationExceptionNoRootCauseShouldReturnConflictDefaultMessage() {
        DataIntegrityViolationException ex = new DataIntegrityViolationException("Direct message");

        ResponseEntity<ApiResponse<Void>> response = handler.handleDataIntegrityViolationException(ex);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertFalse(response.getBody().isExito());
        assertEquals("Error de integridad de datos (duplicidad o restricción violada).", response.getBody().getMensaje());
    }

    @Test
    void handleDataIntegrityViolationExceptionNullMessageShouldReturnConflictDefaultMessage() {
        DataIntegrityViolationException ex = mock(DataIntegrityViolationException.class);
        when(ex.getRootCause()).thenReturn(null);
        when(ex.getMessage()).thenReturn(null);

        ResponseEntity<ApiResponse<Void>> response = handler.handleDataIntegrityViolationException(ex);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertFalse(response.getBody().isExito());
        assertEquals("Error de integridad de datos (duplicidad o restricción violada).", response.getBody().getMensaje());
    }

    @Test
    void handleValidacionNegocioWithListShouldReturnBadRequest() {
        List<String> errores = List.of("Error 1", "Error 2");
        ValidacionNegocioException ex = new ValidacionNegocioException(errores);

        ResponseEntity<ApiResponse<List<String>>> response = handler.handleValidacionNegocio(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertFalse(response.getBody().isExito());
        assertEquals(errores, response.getBody().getData());
    }

    @Test
    void handleValidacionNegocioWithSingleMessageShouldReturnBadRequest() {
        ValidacionNegocioException ex = new ValidacionNegocioException("Error unico");

        ResponseEntity<ApiResponse<List<String>>> response = handler.handleValidacionNegocio(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertFalse(response.getBody().isExito());
        assertEquals(List.of("Error unico"), response.getBody().getData());
    }

    @Test
    void handleHttpMessageNotReadableShouldReturnBadRequest() {
        HttpMessageNotReadableException ex = mock(HttpMessageNotReadableException.class);
        when(ex.getMessage()).thenReturn("Malformed JSON");

        ResponseEntity<ApiResponse<Void>> response = handler.handleHttpMessageNotReadable(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertFalse(response.getBody().isExito());
        assertEquals("Error al procesar el cuerpo de la petición. Formato inválido.", response.getBody().getMensaje());
    }
}
