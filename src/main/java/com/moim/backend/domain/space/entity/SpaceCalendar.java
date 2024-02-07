package com.moim.backend.domain.space.entity;

import com.moim.backend.domain.user.entity.Users;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static jakarta.persistence.FetchType.*;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Entity
@AllArgsConstructor(access = PRIVATE)
@NoArgsConstructor(access = PROTECTED)
@Getter
@Builder
public class SpaceCalendar {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long spaceCalendarId;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "space_id")
    private Space space;

    @NotNull
    private String scheduleName;

    @NotNull
    private LocalDateTime date;

    @NotNull
    private String dayOfWeek;

    @NotNull
    private String note;

    private String locationName;
}
