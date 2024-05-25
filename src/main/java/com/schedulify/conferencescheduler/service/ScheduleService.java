package com.schedulify.conferencescheduler.service;

import com.schedulify.conferencescheduler.web.model.v1.request.SchedulePresentationsRequest;
import com.schedulify.conferencescheduler.web.model.v1.response.SchedulePresentationsResponse;

public interface ScheduleService {
    SchedulePresentationsResponse schedule(SchedulePresentationsRequest request);
}
