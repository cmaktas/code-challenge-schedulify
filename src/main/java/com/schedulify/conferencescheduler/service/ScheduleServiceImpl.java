package com.schedulify.conferencescheduler.service;

import com.schedulify.conferencescheduler.domain.dto.EventDto;
import com.schedulify.conferencescheduler.mapper.ScheduleServiceMapper;
import com.schedulify.conferencescheduler.web.model.v1.request.SchedulePresentationsRequest;
import com.schedulify.conferencescheduler.web.model.v1.response.SchedulePresentationsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ScheduleServiceImpl implements ScheduleService{

    private final ScheduleServiceMapper scheduleServiceMapper;

    @Override
    public SchedulePresentationsResponse schedule(SchedulePresentationsRequest request) {
        List<EventDto> eventDtos = scheduleServiceMapper.mapToEventDtos(request);
        return null;
    }
}
