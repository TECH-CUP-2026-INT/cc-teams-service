package co.edu.escuelaing.techcup.teams.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ApiResponseTest {

    @Test
    void constructor_withArgs_setsFields() {
        ApiResponse response = new ApiResponse("Operation successful", true);

        assertEquals("Operation successful", response.getMessage());
        assertTrue(response.isSuccess());
    }

    @Test
    void noArgsConstructor_andSetters_work() {
        ApiResponse response = new ApiResponse();
        response.setMessage("Error occurred");
        response.setSuccess(false);

        assertEquals("Error occurred", response.getMessage());
        assertFalse(response.isSuccess());
    }
}
