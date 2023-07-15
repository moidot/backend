package com.moim.backend.domain.space.service;

import com.moim.backend.domain.space.Repository.GroupRepository;
import com.moim.backend.domain.space.Repository.ParticipationRepository;
import com.moim.backend.domain.space.entity.Groups;
import com.moim.backend.domain.space.entity.Participation;
import com.moim.backend.domain.space.entity.TransportationType;
import com.moim.backend.domain.space.request.GroupServiceRequest;
import com.moim.backend.domain.space.response.GroupResponse;
import com.moim.backend.global.common.Result;
import com.moim.backend.global.common.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final ParticipationRepository participationRepository;


    public GroupResponse.Create createGroup(GroupServiceRequest.Create request) {

        Groups group = groupRepository.save(
                Groups.builder()
                        // 유저 미구현 상태로 인한 ID값 임시 랜덤
                        .adminId((long) ((Math.random() * 10000) + 1))
                        .name(request.getName())
                        .date(request.getDate())
                        .place("none")
                        .build()
        );

        return GroupResponse.Create.response(group);
    }

    public GroupResponse.Participate participateGroup(GroupServiceRequest.Participate request) {
        if (!request.getTransportation().equals("BUS") && !request.getTransportation().equals("SUBWAY")) {
            throw new CustomException(Result.INVALID_TRANSPORTATION);
        }

        Groups group = groupRepository.findById(request.getGroupId())
                .orElseThrow(
                        () -> new CustomException(Result.NOT_FOUND_GROUP)
                );

        Participation participation = participationRepository.save(
                Participation.builder()
                        .group(group)
                        // 유저 미구현 상태로 인한 userId 및 이름 고정
                        .userId(1L)
                        .userName("JWT 미구현")
                        .latitude(request.getLatitude())
                        .longitude(request.getLongitude())
                        .transportation(TransportationType.valueOf(request.getTransportation()))
                        .password(request.getPassword())
                        .build()
        );

        return GroupResponse.Participate.response(participation);
    }
}
