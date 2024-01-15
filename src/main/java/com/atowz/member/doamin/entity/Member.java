package com.atowz.member.doamin.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PROTECTED)
public class Member {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private String username;
    private String nickname;
    private String recommendCode;
    private int recommendCount;

    @ElementCollection
    @CollectionTable(name = "member_profile_img", joinColumns = @JoinColumn(name = "member_id"))
    private List<String> profileImg = new ArrayList<>();

    @Enumerated (STRING)
    private MemberStatus status;

    @Embedded
    private MyProfile profile;

    @Embedded
    private Interview interview;

    @Embedded
    private Exam exam;

    private Boolean useAgreement;
    private Boolean personalInformation;
}