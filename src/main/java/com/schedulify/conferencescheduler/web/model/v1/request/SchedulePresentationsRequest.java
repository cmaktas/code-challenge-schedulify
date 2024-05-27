package com.schedulify.conferencescheduler.web.model.v1.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SchedulePresentationsRequest {

    @NotNull
    @Schema(description = "List of presentations to be scheduled")
    private List<Presentation> presentations;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Presentation {
        @NotBlank
        @Pattern(regexp = ".*\\S.*\\S.*", message = "The subject must contain at least two non-whitespace characters")
        @Schema(description = "The subject of the presentation", example = "Architecting Your Codebase")
        private String subject;

        @NotBlank(message = "Duration cannot be empty")
        @Pattern(regexp = "\\d+|lightning", message = "Duration must be a positive integer or 'lightning'")
        @Schema(description = "The duration of the presentation in minutes or 'lightning' for 5-minute presentations", example = "60")
        private String duration;
    }
}
