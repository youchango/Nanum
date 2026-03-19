package com.nanum.user.member.service;

import com.nanum.domain.member.dto.MemberDTO;
import com.nanum.domain.member.dto.PasswordResetRequest;
import com.nanum.domain.member.dto.PasswordResetResponse;
import com.nanum.domain.member.dto.ProfileResponse;
import com.nanum.domain.member.dto.ProfileUpdateRequest;

public interface MemberService {
    void signup(MemberDTO memberDTO);

    ProfileResponse getProfile(String memberId);

    void updateProfile(String memberId, ProfileUpdateRequest request);

    void withdraw(String memberId, String password);

    PasswordResetResponse resetPassword(PasswordResetRequest request);
}
