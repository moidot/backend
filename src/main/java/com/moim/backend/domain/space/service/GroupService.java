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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.StringTokenizer;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final ParticipationRepository participationRepository;

    public GroupResponse.Create createGroup(GroupServiceRequest.Create request, Users user) {
        Groups group = groupRepository.save(
                Groups.builder()
                        .adminId(user.getUserId())
                        .name(request.getName())
                        .date(parseLocalDate(request.getDate()))
                        .place("none")
                        .build()
        );

        return GroupResponse.Create.response(group);
    }

    public GroupResponse.Participate participateGroup(GroupServiceRequest.Participate request, Users user) {
        if (!request.getTransportation().equals("BUS") && !request.getTransportation().equals("SUBWAY")) {
            throw new CustomException(Result.INVALID_TRANSPORTATION);
        }
        Groups group = groupRepository.findById(request.getGroupId())
                .orElseThrow(
                        () -> new CustomException(Result.NOT_FOUND_GROUP)
                );
        String encryptedPassword = (request.getPassword() != null) ?
                encrypt(request.getPassword()) : null;

        Participation participation = participationRepository.save(
                Participation.builder()
                        .group(group)
                        .userId(user.getUserId())
                        .userName(user.getName())
                        .latitude(request.getLatitude())
                        .longitude(request.getLongitude())
                        .transportation(TransportationType.valueOf(request.getTransportation()))
                        .password(encryptedPassword)
                        .build()
        );

        return GroupResponse.Participate.response(participation);
    }

    // method
    public static String encrypt(String password) {
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
            throw new CustomException(Result.FAIL);
        }
    }

    private static LocalDate parseLocalDate(String strDate) {
        LocalDate date = null;
        if (strDate != null) {
            StringTokenizer st = new StringTokenizer(strDate, "-");
            int year = Integer.parseInt(st.nextToken());
            int month = Integer.parseInt(st.nextToken());
            int day = Integer.parseInt(st.nextToken());
            date = LocalDate.of(year, month, day);
        }
        return date;
    }
}
