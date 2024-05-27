package com.schedulify.conferencescheduler.mapper;

import com.schedulify.conferencescheduler.domain.dto.EventDto;
import com.schedulify.conferencescheduler.domain.enums.EventType;
import com.schedulify.conferencescheduler.web.model.v1.request.SchedulePresentationsRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ScheduleServiceMapperTest {

    @Autowired
    private ScheduleServiceMapper scheduleServiceMapper;

    @Test
    void testMapToEventDtos() {
        List<SchedulePresentationsRequest.Presentation> presentations = List.of(
                new SchedulePresentationsRequest.Presentation("Test Presentation", "60"),
                new SchedulePresentationsRequest.Presentation("Lightning Presentation", "lightning")
        );

        SchedulePresentationsRequest request = new SchedulePresentationsRequest(presentations);

        List<EventDto> eventDtos = scheduleServiceMapper.mapToEventDtos(request);

        assertNotNull(eventDtos);
        assertEquals(2, eventDtos.size());
        assertEquals(EventType.PRESENTATION, eventDtos.get(0).getEventType());
        assertEquals(60, eventDtos.get(0).getDurationInMinutes());
        assertEquals(5, eventDtos.get(1).getDurationInMinutes());
    }
}
