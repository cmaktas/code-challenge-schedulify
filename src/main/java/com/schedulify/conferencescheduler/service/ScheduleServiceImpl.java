package com.schedulify.conferencescheduler.service;

import com.schedulify.conferencescheduler.domain.dto.EventDto;
import com.schedulify.conferencescheduler.domain.enums.EventType;
import com.schedulify.conferencescheduler.exception.CustomValidationException;
import com.schedulify.conferencescheduler.mapper.ScheduleServiceMapper;
import com.schedulify.conferencescheduler.web.model.v1.request.SchedulePresentationsRequest;
import com.schedulify.conferencescheduler.web.model.v1.response.SchedulePresentationsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleServiceMapper scheduleServiceMapper;
    private final DateTimeFormatter timeFormatter;

    @Override
    public SchedulePresentationsResponse schedule(SchedulePresentationsRequest request) {
        log.info("Received schedule request with {} presentations", request.getPresentations().size());

        // Validate request data integrity
        log.debug("Validating presentations...");
        validatePresentations(request.getPresentations());
        log.debug("Validation completed successfully");

        // Mapping request array elements to EventDto objects for further processing
        List<EventDto> eventDtos = scheduleServiceMapper.mapToEventDtos(request);

        // Getting data ready by sorting presentations by duration longest -> shortest
        List<EventDto> filteredAndSortedEventDtos = sortPresentations(eventDtos);

        // Prepare empty track array for response data
        List<SchedulePresentationsResponse.Track> tracks = new ArrayList<>();

        // Renaming EventDto list more meaningfully for future operations
        List<EventDto> remainingPresentations = new ArrayList<>(filteredAndSortedEventDtos);
        int trackNumber = 1;

        while (!ObjectUtils.isEmpty(remainingPresentations)) {
            log.info("Allocating presentations for track {}", trackNumber);
            // Scheduling presentations and building structured sessions by rules
            List<EventDto> sessionEvents = allocatePresentations(remainingPresentations);

            // Build and add the track to the response
            tracks.add(buildTrack(trackNumber, sessionEvents));
            log.info("Track {} allocated with {} events", trackNumber, sessionEvents.size());

            trackNumber++;
        }

        // Create and return the final response
        SchedulePresentationsResponse response = SchedulePresentationsResponse.builder()
                .status("Success")
                .message("Successfully scheduled events under " + trackNumber + " tracks.")
                .timestamp(LocalDateTime.now())
                .data(tracks)
                .build();

        log.info("Schedule processing completed successfully");
        return response;
    }

    private List<EventDto> sortPresentations(List<EventDto> eventDtos) {
        return eventDtos.stream()
                .sorted(Comparator.comparingInt(EventDto::getDurationInMinutes).reversed())
                .collect(Collectors.toList());
    }

    private List<EventDto> allocatePresentations(List<EventDto> presentations) {
        List<EventDto> sessionEvents = new ArrayList<>();
        // Set time to the start of the conference 9:00AM
        LocalTime currentTime = LocalTime.of(9, 0);

        List<EventDto> unallocatedPresentations = new ArrayList<>(presentations);

        // Allocate morning session (3 hours)
        log.debug("Allocating morning session presentations");
        currentTime = addPresentationsToSession(sessionEvents, unallocatedPresentations, currentTime, 180);

        // Add lunch break
        addLunchBreak(sessionEvents);
        currentTime = LocalTime.of(13, 0);

        // Allocate afternoon session (4 hours)
        log.debug("Allocating afternoon session presentations");
        currentTime = addPresentationsToSession(sessionEvents, unallocatedPresentations, currentTime, 240);

        // Add networking event if there is time left after all presentations & after 4:00 PM
        addNetworkingEvent(sessionEvents, currentTime);

        // Refresh the list to add unallocated presentations
        presentations.clear();
        presentations.addAll(unallocatedPresentations);

        return sessionEvents;
    }

    private LocalTime addPresentationsToSession(List<EventDto> sessionEvents, List<EventDto> unallocatedPresentations, LocalTime startTime, int sessionDurationInMinutes) {
        // Set full session duration for morning sessions: 180 & for afternoon sessions: 240
        long remainingTime = sessionDurationInMinutes;
        LocalTime currentTime = startTime;

        while (remainingTime > 0 && !unallocatedPresentations.isEmpty()) {
            final long finalRemainingTime = remainingTime;
            // Finding the longest duration presentation that fits remaining time in the session
            EventDto longestPresentation = unallocatedPresentations.stream()
                    .filter(p -> p.getDurationInMinutes() <= finalRemainingTime)
                    .max(Comparator.comparingInt(EventDto::getDurationInMinutes))
                    .orElse(null);
            // Add the presentation and update the remaining time for the session
            if (longestPresentation != null) {
                addPresentation(sessionEvents, unallocatedPresentations, longestPresentation, currentTime);
                remainingTime -= longestPresentation.getDurationInMinutes();
                currentTime = currentTime.plusMinutes(longestPresentation.getDurationInMinutes());
            } else {
                // Look for multiple presentations that may fit to the remaining duration
                List<EventDto> fittingPresentations = findFittingPresentations(unallocatedPresentations, remainingTime);
                if (fittingPresentations != null && !fittingPresentations.isEmpty()) {
                    // Add the presentations and update the remaining time for the session
                    for (EventDto fittingPresentation : fittingPresentations) {
                        addPresentation(sessionEvents, unallocatedPresentations, fittingPresentation, currentTime);
                        remainingTime -= fittingPresentation.getDurationInMinutes();
                        currentTime = currentTime.plusMinutes(fittingPresentation.getDurationInMinutes());
                    }
                } else {
                    break;
                }
            }
        }

        return currentTime;
    }

    private List<EventDto> findFittingPresentations(List<EventDto> presentations, long remainingTime) {
        List<EventDto> fittingPresentations = new ArrayList<>();
        long accumulatedTime = 0;

        for (EventDto presentation : presentations) {
            if (accumulatedTime + presentation.getDurationInMinutes() <= remainingTime) {
                accumulatedTime += presentation.getDurationInMinutes();
                fittingPresentations.add(presentation);
                if (accumulatedTime == remainingTime) {
                    return fittingPresentations;
                }
            }
        }

        return accumulatedTime > 0 ? fittingPresentations : null;
    }

    private void addPresentation(List<EventDto> sessionEvents, List<EventDto> unallocatedPresentations, EventDto presentation, LocalTime currentTime) {
        presentation.setStartsAt(currentTime.format(timeFormatter));
        presentation.setEndsAt(currentTime.plusMinutes(presentation.getDurationInMinutes()).format(timeFormatter));
        sessionEvents.add(presentation);
        unallocatedPresentations.remove(presentation);
        log.debug("Added presentation: {} from {} to {}", presentation.getSubject(), presentation.getStartsAt(), presentation.getEndsAt());
    }

    private void addLunchBreak(List<EventDto> sessionEvents) {
        EventDto lunchBreak = EventDto.builder()
                .eventType(EventType.LUNCH)
                .subject("Lunch")
                .durationInMinutes(60)
                .startsAt("12:00PM")
                .endsAt("01:00PM")
                .build();

        sessionEvents.add(lunchBreak);
        log.debug("Added lunch break from 12:00PM to 01:00PM");
    }

    private void addNetworkingEvent(List<EventDto> sessionEvents, LocalTime currentTime) {
        if (currentTime.isBefore(LocalTime.of(17, 0)) && currentTime.isAfter(LocalTime.of(16, 0))) {
            int networkingDuration = (int) LocalTime.of(17, 0).toSecondOfDay() / 60 - (int) currentTime.toSecondOfDay() / 60;
            if (networkingDuration > 0) {
                EventDto networkingEvent = EventDto.builder()
                        .eventType(EventType.NETWORKING)
                        .subject("Networking Event")
                        .durationInMinutes(networkingDuration)
                        .startsAt(currentTime.format(timeFormatter))
                        .endsAt("05:00PM")
                        .build();

                sessionEvents.add(networkingEvent);
                log.debug("Added networking event from {} to 05:00PM", currentTime.format(timeFormatter));
            }
        }
    }

    private SchedulePresentationsResponse.Track buildTrack(int trackNumber, List<EventDto> sessionEvents) {
        List<SchedulePresentationsResponse.EventWrapper> events = sessionEvents.stream()
                .map(event -> SchedulePresentationsResponse.EventWrapper.builder().event(event).build())
                .collect(Collectors.toList());

        log.debug("Built track {} with {} events", trackNumber, events.size());
        return SchedulePresentationsResponse.Track.builder()
                .trackNo(trackNumber)
                .track(events)
                .build();
    }

    private void validatePresentations(List<SchedulePresentationsRequest.Presentation> presentations) {
        Set<String> subjects = new HashSet<>();
        for (SchedulePresentationsRequest.Presentation presentation : presentations) {
            if (presentation.getSubject().trim().length() < 2) {
                throw new CustomValidationException("The subject must contain at least two non-whitespace characters");
            }
            if (presentation.getDuration().trim().isEmpty()) {
                throw new CustomValidationException("Duration cannot be empty");
            }
            if (!presentation.getDuration().matches("\\d+|(?i)lightning")) {
                throw new CustomValidationException("Duration must be a positive integer or 'lightning', 'LIGHTNING', or 'Lightning'");
            }
            if (!subjects.add(presentation.getSubject().trim())) {
                throw new CustomValidationException("Duplicate presentation subject: " + presentation.getSubject().trim());
            }
            int duration = "lightning".equalsIgnoreCase(presentation.getDuration().trim()) ? 5 : Integer.parseInt(presentation.getDuration().trim());
            if (duration <= 0 || duration > 240) {
                throw new CustomValidationException("Duration must be between 1 and 240 minutes");
            }
        }
    }
}
