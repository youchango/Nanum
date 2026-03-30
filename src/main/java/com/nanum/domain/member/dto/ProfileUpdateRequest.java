package com.nanum.domain.member.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProfileUpdateRequest {
    @NotBlank(message = "이름은 필수입니다.")
    private String memberName;

    @NotBlank(message = "휴대전화번호는 필수입니다.")
    private String mobilePhone;

    private String email;
    private String zipcode;
    private String address;
    private String addressDetail;
    private String currentPassword; // 기존 비밀번호 (비밀번호 변경 시 필수)
    private String password;       // 새 비밀번호
    private String marketingYn;
}
