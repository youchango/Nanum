package com.nanum.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PasswordResetResponse {

    private String memberId;
    private String tempPassword;
}
