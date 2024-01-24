package com.moim.backend.domain.user.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.*;

@Entity
@AllArgsConstructor(access = PRIVATE)
@NoArgsConstructor(access = PROTECTED)
@Getter
@Builder
public class UserCalendar {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long userCalendarId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users user;

    @NotNull
    private String scheduleName;

    @NotNull
    private LocalDateTime date;

    @NotNull
    private String dayOfWeek;

    @NotNull
    private String note;

    private String locationName;

    @NotNull
    private String color;

}
