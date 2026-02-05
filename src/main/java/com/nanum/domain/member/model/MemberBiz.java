package com.nanum.domain.member.model;

import com.nanum.global.common.dto.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "member_biz")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class MemberBiz extends BaseTimeEntity {

    @Id
    @Column(name = "member_code")
    private String memberCode;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "member_code", referencedColumnName = "member_code")
    private Member member;

    @Column(name = "business_number", nullable = false)
    private String businessNumber;

    @Column(name = "company_name", nullable = false)
    private String companyName;

    @Column(name = "ceo_name", nullable = false)
    private String ceoName;

    @Column(name = "business_type")
    private String businessType;

    @Column(name = "business_item")
    private String businessItem;

    public void setMember(Member member) {
        this.member = member;
        this.memberCode = member.getMemberCode();
    }
}
