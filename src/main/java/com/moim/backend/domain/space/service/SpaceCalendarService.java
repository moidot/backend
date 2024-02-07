package com.moim.backend.domain.space.service;

import com.moim.backend.domain.space.entity.Space;
import com.moim.backend.domain.space.entity.SpaceCalendar;
import com.moim.backend.domain.space.repository.SpaceCalendarRepository;
import com.moim.backend.domain.space.repository.SpaceRepository;
import com.moim.backend.domain.space.request.controller.CreateSpaceCalendarRequest;
import com.moim.backend.domain.space.response.space.CreateSpaceCalendarResponse;
import com.moim.backend.global.common.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.moim.backend.global.common.Result.NOT_FOUND_GROUP;
import static java.time.format.TextStyle.SHORT;
import static java.util.Locale.KOREAN;

@Service
@RequiredArgsConstructor
public class SpaceCalendarService {
    private final SpaceCalendarRepository spaceCalendarRepository;
    private final SpaceRepository spaceRepository;

    public CreateSpaceCalendarResponse createSpaceCalendar(CreateSpaceCalendarRequest request) {
        Space space = spaceRepository.findById(request.getSpaceId()).orElseThrow(
                () -> new CustomException(NOT_FOUND_GROUP)
        );

        SpaceCalendar spaceCalendar = spaceCalendarRepository.save(SpaceCalendar.builder()
                .space(space)
                .scheduleName(request.getScheduleName())
                .date(request.getDate())
                .dayOfWeek(request.getDate().getDayOfWeek().getDisplayName(SHORT, KOREAN))
                .note(request.getNote())
                .locationName(request.getLocationName())
                .build());

        return CreateSpaceCalendarResponse.response(spaceCalendar);
    }
}
