package com.techcup.ccteams.exception;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
public class ExceptionTest {
    @Test
    public void testBusinessException() {
        BusinessException exception = new BusinessException("Error message");
        assertEquals("Error message", exception.getMessage());
    }
}