package co.edu.escuelaing.techcup.teams.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Generic response DTO for simple messages.
 */
@Schema(description = "Standard API response wrapper")
public class ApiResponse {

    @Schema(description = "Human-readable result message", example = "Captaincy successfully transferred to player@techcup.co")
    private String message;

    @Schema(description = "Whether the operation succeeded", example = "true")
    private boolean success;

    public ApiResponse() {}

    public ApiResponse(String message, boolean success) {
        this.message = message;
        this.success = success;
    }

    public String getMessage() { return message; }
    public boolean isSuccess() { return success; }
    public void setMessage(String message) { this.message = message; }
    public void setSuccess(boolean success) { this.success = success; }
}
