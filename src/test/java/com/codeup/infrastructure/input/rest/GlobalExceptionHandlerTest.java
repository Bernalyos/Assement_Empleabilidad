package com.codeup.infrastructure.input.rest;

import com.codeup.domain.exception.BusinessRuleViolationException;
import com.codeup.domain.exception.UnauthorizedActionException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleBusinessRuleViolation_ShouldReturnBadRequestWithProblemDetail() {
        String message = "Business rule violated";
        BusinessRuleViolationException ex = new BusinessRuleViolationException(message);

        ProblemDetail result = handler.handleBusinessRuleViolation(ex);

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getStatus());
        assertEquals("Business Rule Violation", result.getTitle());
        assertEquals(message, result.getDetail());
    }

    @Test
    void handleUnauthorizedAction_ShouldReturnForbiddenWithProblemDetail() {
        String message = "Unauthorized";
        UnauthorizedActionException ex = new UnauthorizedActionException(message);

        ProblemDetail result = handler.handleUnauthorizedAction(ex);

        assertEquals(HttpStatus.FORBIDDEN.value(), result.getStatus());
        assertEquals("Unauthorized Action", result.getTitle());
        assertTrue(result.getDetail().contains("permission"));
    }

    @Test
    void handleRuntimeException_ShouldReturnInternalServerErrorWithProblemDetail() {
        RuntimeException ex = new RuntimeException("Unexpected");

        ProblemDetail result = handler.handleRuntimeException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), result.getStatus());
        assertEquals("Internal Server Error", result.getTitle());
        assertTrue(result.getDetail().contains("unexpected"));
    }
}
