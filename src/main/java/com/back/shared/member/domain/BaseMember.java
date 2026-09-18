package com.back.shared.member.domain;

import com.back.global.jpa.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static lombok.AccessLevel.PROTECTED;

@MappedSuperclass
@Getter
@Setter(value = PROTECTED)
@NoArgsConstructor
public abstract class BaseMember extends BaseEntity {
    @Column(unique = true)
    private String username;
    private String nickname;
    private int activityScore;

    public BaseMember(String username, String nickname, int activityScore) {
        this.username = username;
        this.nickname = nickname;
        this.activityScore = activityScore;
    }
}