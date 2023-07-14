package com.moim.backend.domain.space.service;

import com.moim.backend.domain.space.Repository.GroupRepository;
import com.moim.backend.domain.space.entity.Groups;
import com.moim.backend.domain.space.request.GroupServiceRequest;
import com.moim.backend.domain.space.response.GroupResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GroupService {
    private final GroupRepository groupRepository;

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
}
