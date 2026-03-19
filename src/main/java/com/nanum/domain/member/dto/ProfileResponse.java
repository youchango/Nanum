package com.nanum.domain.member.dto;

import com.nanum.domain.member.model.Member;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class ProfileResponse {
    private String memberId;
    private String memberName;
    private String mobilePhone;
    private String email;
    private String zipcode;
    private String address;
    private String addressDetail;
    private String smsYn;
    private String emailYn;
    private String memberType;
    // 기업회원 정보
    private String companyName;
    private String ceoName;
    private String businessNumber;
    private String businessType;
    private String businessItem;

    public static ProfileResponse from(Member member) {
        return ProfileResponse.builder()
                .memberId(member.getMemberId())
                .memberName(member.getMemberName())
                .mobilePhone(member.getMobilePhone())
                .email(member.getEmail())
                .zipcode(member.getZipcode())
                .address(member.getAddress())
                .addressDetail(member.getAddressDetail())
                .smsYn(member.getSmsYn())
                .emailYn(member.getEmailYn())
                .memberType(member.getMemberType() != null ? member.getMemberType().name() : "U")
                .build();
    }

    public static ProfileResponse from(Member member, com.nanum.domain.member.model.MemberBiz biz) {
        ProfileResponseBuilder builder = from(member).toBuilder();
        if (biz != null) {
            builder.companyName(biz.getCompanyName())
                   .ceoName(biz.getCeoName())
                   .businessNumber(biz.getBusinessNumber())
                   .businessType(biz.getBusinessType())
                   .businessItem(biz.getBusinessItem());
        }
        return builder.build();
    }
}
