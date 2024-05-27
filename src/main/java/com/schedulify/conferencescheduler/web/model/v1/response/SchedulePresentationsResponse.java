package com.schedulify.conferencescheduler.web.model.v1.response;

import com.schedulify.conferencescheduler.domain.dto.EventDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@AllArgsConstructor
public class SchedulePresentationsResponse extends BaseResponse<List<SchedulePresentationsResponse.Track>> {

    @Data
    @SuperBuilder
    @AllArgsConstructor
    public static class Track {

        @Schema(description = "Number of the track")
        private int trackNo;

        @Schema(description = "List of events in the track")
        private List<EventWrapper> track;
    }

    @Data
    @SuperBuilder
    @AllArgsConstructor
    public static class EventWrapper {
        @Schema(description = "Event object containing event details")
        private EventDto event;
    }
}
