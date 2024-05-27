package com.schedulify.conferencescheduler.web.model.v1.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class BaseResponse<T> {

    @Schema(description = "Status of the response", example = "Success")
    private String status;

    @Schema(description = "Message detailing the response", example = "Request processed successfully")
    private String message;

    @Schema(description = "Timestamp of the response", example = "2024-05-25T18:00:00")
    private LocalDateTime timestamp;

    @Schema(description = "Response data")
    private T data;
}
