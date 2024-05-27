package com.schedulify.conferencescheduler.mapper;

import com.schedulify.conferencescheduler.domain.dto.EventDto;
import com.schedulify.conferencescheduler.domain.enums.EventType;
import com.schedulify.conferencescheduler.web.model.v1.request.SchedulePresentationsRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ScheduleServiceMapper {

    public List<EventDto> mapToEventDtos(SchedulePresentationsRequest request) {
        return request.getPresentations().stream()
                .map(presentation -> {
                    int durationInMinutes = "lightning".equalsIgnoreCase(presentation.getDuration().trim()) ? 5 : Integer.parseInt(presentation.getDuration().trim());
                    return EventDto.builder()
                            .eventType(EventType.PRESENTATION)
                            .subject(presentation.getSubject().trim())
                            .durationInMinutes(durationInMinutes)
                            .startsAt(null)
                            .endsAt(null)
                            .build();
                })
                .collect(Collectors.toList());
    }
}
