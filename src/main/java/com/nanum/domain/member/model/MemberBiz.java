package com.nanum.domain.member.model;

import com.nanum.global.common.dto.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "member_biz")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class MemberBiz extends BaseEntity {

    @Id
    @Column(name = "member_code")
    private String memberCode;

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

    public void setMemberCode(String memberCode) {
        this.memberCode = memberCode;
    }
}

