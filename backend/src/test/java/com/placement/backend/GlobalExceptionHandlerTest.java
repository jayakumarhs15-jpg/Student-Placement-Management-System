package com.placement.backend;

import com.placement.backend.exception.GlobalExceptionHandler;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;
    private HttpServletRequest mockRequest;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
        mockRequest = mock(HttpServletRequest.class);
        when(mockRequest.getRequestURI()).thenReturn("/api/test");
    }

    @Test
    void handleResponseStatusExceptionShouldReturnAppropriateStatusAndBody() {
        ResponseStatusException ex = new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found");

        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleResponseStatusException(ex, mockRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals(404, body.get("status"));
        assertEquals("Resource not found", body.get("message"));
        assertEquals("/api/test", body.get("path"));
    }

    @Test
    void handleValidationExceptionsShouldReturnFieldErrors() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("student", "email", "must not be blank");

        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleValidationExceptions(ex, mockRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals(400, body.get("status"));
        assertEquals("Validation Failed", body.get("error"));
        assertTrue(body.containsKey("errors"));
    }

    @Test
    void handleGenericExceptionWithCustomMessageShouldReturn500() {
        Exception ex = new RuntimeException("Custom database error");

        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleGenericException(ex, mockRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals(500, body.get("status"));
        assertEquals("Custom database error", body.get("message"));
    }

    @Test
    void handleGenericExceptionWithNullMessageShouldFallbackToDefaultMessage() {
        Exception ex = new NullPointerException();

        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleGenericException(ex, mockRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals(500, body.get("status"));
        assertEquals("An unexpected error occurred", body.get("message"));
    }
}
