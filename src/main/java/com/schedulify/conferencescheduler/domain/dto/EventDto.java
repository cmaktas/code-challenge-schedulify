package com.schedulify.conferencescheduler.domain.dto;

import com.schedulify.conferencescheduler.domain.enums.EventType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventDto {

    private EventType eventType;
    private String subject;
    private int durationInMinutes;
    private String startsAt;
    private String endsAt;
}
