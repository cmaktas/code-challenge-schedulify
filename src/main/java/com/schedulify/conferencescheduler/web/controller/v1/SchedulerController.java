package com.schedulify.conferencescheduler.web.controller.v1;

import com.schedulify.conferencescheduler.service.ScheduleService;
import com.schedulify.conferencescheduler.web.model.v1.request.SchedulePresentationsRequest;
import com.schedulify.conferencescheduler.web.model.v1.response.SchedulePresentationsResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/v1/schedule")
public class SchedulerController {

    private final ScheduleService scheduleService;

    @Operation(summary = "Schedule presentations", description = "Schedules presentations for the conference")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully scheduled presentations"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SchedulePresentationsResponse> schedulePresentations(@RequestBody SchedulePresentationsRequest request) {
        log.debug("Received schedule presentations request: {}", request);
        //TODO response debug log will be added here
        SchedulePresentationsResponse schedulePresentationsResponse = scheduleService.schedule(request);
        log.debug("Schedule service response: {}", schedulePresentationsResponse);
        return ResponseEntity.ok(schedulePresentationsResponse);
    }

}
