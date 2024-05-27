package com.schedulify.conferencescheduler.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testHandleCustomValidationException() {
        CustomValidationException ex = new CustomValidationException("Test error");
        ResponseEntity<Object> response = globalExceptionHandler.handleCustomValidationException(ex);
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Test error", ((Map<String, String>) response.getBody()).get("error"));
    }

    @Test
    void testHandleValidationExceptions() {
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("objectName", "fieldName", "defaultMessage");
        when(bindingResult.getAllErrors()).thenReturn(List.of(fieldError));

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);
        ResponseEntity<Map<String, String>> response = globalExceptionHandler.handleValidationExceptions(ex);
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("defaultMessage", response.getBody().get("fieldName"));
    }
}
