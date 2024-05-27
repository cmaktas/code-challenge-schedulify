package com.schedulify.conferencescheduler.service;

import com.schedulify.conferencescheduler.domain.dto.EventDto;
import com.schedulify.conferencescheduler.domain.enums.EventType;
import com.schedulify.conferencescheduler.exception.CustomValidationException;
import com.schedulify.conferencescheduler.mapper.ScheduleServiceMapper;
import com.schedulify.conferencescheduler.web.model.v1.request.SchedulePresentationsRequest;
import com.schedulify.conferencescheduler.web.model.v1.response.SchedulePresentationsResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.Method;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class ScheduleServiceImplTest {

    @Mock
    private ScheduleServiceMapper scheduleServiceMapper;

    @InjectMocks
    private ScheduleServiceImpl scheduleService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        scheduleService = new ScheduleServiceImpl(scheduleServiceMapper, DateTimeFormatter.ofPattern("hh:mma"));
    }

    @Test
    void testScheduleValidRequest() {
        List<SchedulePresentationsRequest.Presentation> presentations = List.of(
                new SchedulePresentationsRequest.Presentation("Test Presentation", "60"),
                new SchedulePresentationsRequest.Presentation("Another Presentation", "30")
        );

        SchedulePresentationsRequest request = new SchedulePresentationsRequest(presentations);

        List<EventDto> eventDtos = List.of(
                EventDto.builder().subject("Test Presentation").durationInMinutes(60).eventType(EventType.PRESENTATION).build(),
                EventDto.builder().subject("Another Presentation").durationInMinutes(30).eventType(EventType.PRESENTATION).build()
        );

        when(scheduleServiceMapper.mapToEventDtos(any(SchedulePresentationsRequest.class))).thenReturn(eventDtos);

        SchedulePresentationsResponse response = scheduleService.schedule(request);

        assertNotNull(response);
        assertEquals("Success", response.getStatus());
        verify(scheduleServiceMapper, times(1)).mapToEventDtos(any(SchedulePresentationsRequest.class));
    }

    @Test
    void testValidatePresentationsInvalidSubject() {
        List<SchedulePresentationsRequest.Presentation> presentations = List.of(
                new SchedulePresentationsRequest.Presentation(" ", "60")
        );

        CustomValidationException exception = assertThrows(CustomValidationException.class,
                () -> invokeValidatePresentations(presentations));

        assertEquals("The subject must contain at least two non-whitespace characters", exception.getMessage());
    }

    @Test
    void testValidatePresentationsInvalidDuration() {
        List<SchedulePresentationsRequest.Presentation> presentations = List.of(
                new SchedulePresentationsRequest.Presentation("Test", "")
        );

        CustomValidationException exception = assertThrows(CustomValidationException.class,
                () -> invokeValidatePresentations(presentations));

        assertEquals("Duration cannot be empty", exception.getMessage());
    }

    @Test
    void testValidatePresentationsDuplicateSubject() {
        List<SchedulePresentationsRequest.Presentation> presentations = List.of(
                new SchedulePresentationsRequest.Presentation("Test", "60"),
                new SchedulePresentationsRequest.Presentation("Test", "30")
        );

        CustomValidationException exception = assertThrows(CustomValidationException.class,
                () -> invokeValidatePresentations(presentations));

        assertEquals("Duplicate presentation subject: Test", exception.getMessage());
    }

    @Test
    void testValidatePresentationsInvalidDurationRange() {
        List<SchedulePresentationsRequest.Presentation> presentations = List.of(
                new SchedulePresentationsRequest.Presentation("Test", "300")
        );

        CustomValidationException exception = assertThrows(CustomValidationException.class,
                () -> invokeValidatePresentations(presentations));

        assertEquals("Duration must be between 1 and 240 minutes", exception.getMessage());
    }

    @Test
    void testSortPresentations() {
        List<EventDto> eventDtos = new ArrayList<>();
        eventDtos.add(EventDto.builder().subject("Short").durationInMinutes(30).eventType(EventType.PRESENTATION).build());
        eventDtos.add(EventDto.builder().subject("Long").durationInMinutes(60).eventType(EventType.PRESENTATION).build());

        List<EventDto> sortedEventDtos = invokeSortPresentations(eventDtos);

        assertEquals("Long", sortedEventDtos.get(0).getSubject());
        assertEquals("Short", sortedEventDtos.get(1).getSubject());
    }

    @Test
    void testAllocatePresentations() {
        List<EventDto> eventDtos = List.of(
                EventDto.builder().subject("Presentation 1").durationInMinutes(60).eventType(EventType.PRESENTATION).build(),
                EventDto.builder().subject("Presentation 2").durationInMinutes(30).eventType(EventType.PRESENTATION).build()
        );

        List<EventDto> allocatedEvents = invokeAllocatePresentations(new ArrayList<>(eventDtos)); // Use a mutable list

        assertNotNull(allocatedEvents);
        assertTrue(allocatedEvents.size() > 0);
    }

    @Test
    void testAddPresentationsToSession() {
        List<EventDto> sessionEvents = new ArrayList<>();
        List<EventDto> unallocatedPresentations = new ArrayList<>();
        unallocatedPresentations.add(EventDto.builder().subject("Presentation 1").durationInMinutes(60).eventType(EventType.PRESENTATION).build());
        unallocatedPresentations.add(EventDto.builder().subject("Presentation 2").durationInMinutes(30).eventType(EventType.PRESENTATION).build());

        invokeAddPresentationsToSession(sessionEvents, unallocatedPresentations, LocalTime.of(9, 0), 180);

        assertFalse(sessionEvents.isEmpty());
        assertEquals(2, sessionEvents.size());
    }

    // Helper methods to invoke private methods using reflection

    private void invokeValidatePresentations(List<SchedulePresentationsRequest.Presentation> presentations) {
        try {
            Method method = ScheduleServiceImpl.class.getDeclaredMethod("validatePresentations", List.class);
            method.setAccessible(true);
            method.invoke(scheduleService, presentations);
        } catch (Exception e) {
            if (e.getCause() instanceof CustomValidationException) {
                throw (CustomValidationException) e.getCause();
            }
            fail("Exception during reflection invocation: " + e.getMessage());
        }
    }

    private List<EventDto> invokeSortPresentations(List<EventDto> eventDtos) {
        try {
            Method method = ScheduleServiceImpl.class.getDeclaredMethod("sortPresentations", List.class);
            method.setAccessible(true);
            return (List<EventDto>) method.invoke(scheduleService, eventDtos);
        } catch (Exception e) {
            fail("Exception during reflection invocation: " + e.getMessage());
            return null;
        }
    }

    private List<EventDto> invokeAllocatePresentations(List<EventDto> eventDtos) {
        try {
            Method method = ScheduleServiceImpl.class.getDeclaredMethod("allocatePresentations", List.class);
            method.setAccessible(true);
            return (List<EventDto>) method.invoke(scheduleService, eventDtos);
        } catch (Exception e) {
            fail("Exception during reflection invocation: " + e.getMessage());
            return null;
        }
    }

    private void invokeAddPresentationsToSession(List<EventDto> sessionEvents, List<EventDto> unallocatedPresentations, LocalTime startTime, int sessionDurationInMinutes) {
        try {
            Method method = ScheduleServiceImpl.class.getDeclaredMethod("addPresentationsToSession", List.class, List.class, LocalTime.class, int.class);
            method.setAccessible(true);
            method.invoke(scheduleService, sessionEvents, unallocatedPresentations, startTime, sessionDurationInMinutes);
        } catch (Exception e) {
            fail("Exception during reflection invocation: " + e.getMessage());
        }
    }
}
