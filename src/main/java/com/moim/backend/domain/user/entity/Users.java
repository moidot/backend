package com.moim.backend.domain.user.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @NotNull(message = "email은 null이 될 수 없습니다.")
    private String email;

    @NotNull(message = "name은 null이 될 수 없습니다.")
    private String name;

    public Users update(String name) {
        this.name = name;
        return this;
    }

    @Builder
    public Users(String email, String name) {
        this.email = email;
        this.name = name;
    }

}
