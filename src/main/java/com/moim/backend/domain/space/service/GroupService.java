package com.moim.backend.domain.space.service;

import com.moim.backend.domain.space.Repository.GroupRepository;
import com.moim.backend.domain.space.Repository.ParticipationRepository;
import com.moim.backend.domain.space.entity.Groups;
import com.moim.backend.domain.space.entity.Participation;
import com.moim.backend.domain.space.entity.TransportationType;
import com.moim.backend.domain.space.request.GroupServiceRequest;
import com.moim.backend.domain.space.response.GroupResponse;
import com.moim.backend.domain.user.entity.Users;
import com.moim.backend.global.common.Result;
import com.moim.backend.global.common.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static com.moim.backend.global.common.Result.*;
import static com.moim.backend.global.common.Result.NOT_FOUND_PARTICIPATE;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final ParticipationRepository participationRepository;

    // 모임 생성
    @Transactional
    public GroupResponse.Create createGroup(GroupServiceRequest.Create request, Users user) {
        Groups group = groupRepository.save(toGroupEntity(request, user));

        return GroupResponse.Create.response(group);
    }

    // 모임 참여
    @Transactional
    public GroupResponse.Participate participateGroup(GroupServiceRequest.Participate request, Users user) {
        if (!request.getTransportation().equals("BUS") && !request.getTransportation().equals("SUBWAY")) {
            throw new CustomException(INVALID_TRANSPORTATION);
        }

        Groups group = getGroup(request);
        String encryptedPassword = encrypt(request.getPassword());
        Participation participation = participationRepository.save(
                toParticipationEntity(request, group, user.getUserId(), encryptedPassword)
        );

        return GroupResponse.Participate.response(participation);
    }

    // 내 참여 정보 수정
    @Transactional
    public GroupResponse.ParticipateUpdate participateUpdate(
            GroupServiceRequest.ParticipateUpdate request, Users user
    ) {
        Participation myParticipate = getParticipate(request);

        if (!myParticipate.getUserId().equals(user.getUserId())) {
            throw new CustomException(NOT_MATCHED_PARTICIPATE);
        }

        myParticipate.update(request);

        return GroupResponse.ParticipateUpdate.response(myParticipate);
    }

    // method

    private Groups getGroup(GroupServiceRequest.Participate request) {
        return groupRepository.findById(request.getGroupId())
                .orElseThrow(
                        () -> new CustomException(NOT_FOUND_GROUP)
                );
    }

    public static String encrypt(String password) {
        if (password == null) return null;
        else {
            try {
                StringBuilder sb = new StringBuilder();
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                md.update(password.getBytes());
                byte[] bytes = md.digest();
                for (byte aByte : bytes) {
                    sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
                }
                return sb.toString();
            } catch (NoSuchAlgorithmException e) {
                throw new CustomException(FAIL);
            }
        }
    }

    private Groups toGroupEntity(GroupServiceRequest.Create request, Users user) {
        return Groups.builder()
                .adminId(user.getUserId())
                .name(request.getName())
                .date(request.getDate())
                .build();
    }

    private Participation toParticipationEntity(
            GroupServiceRequest.Participate request, Groups group, long userId, String encryptedPassword
    ) {
        return Participation.builder()
                .group(group)
                .userId(userId)
                .userName(request.getUserName())
                .locationName(request.getLocationName())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .transportation(TransportationType.valueOf(request.getTransportation()))
                .password(encryptedPassword)
                .build();
    }

    private Participation getParticipate(GroupServiceRequest.ParticipateUpdate request) {
        return participationRepository.findById(request.getParticipateId()).orElseThrow(
                () -> new CustomException(NOT_FOUND_PARTICIPATE)
        );
    }
}
