package com.nanum.domain.member.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PasswordResetRequest {

    @NotBlank(message = "아이디를 입력해주세요.")
    private String memberId;

    @NotBlank(message = "이름을 입력해주세요.")
    private String memberName;

    @NotBlank(message = "휴대전화번호를 입력해주세요.")
    private String mobilePhone;
}
