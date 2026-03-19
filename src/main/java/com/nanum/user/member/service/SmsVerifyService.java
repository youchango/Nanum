package com.nanum.user.member.service;

import com.nanum.domain.member.model.SmsVerify;
import com.nanum.user.member.repository.SmsVerifyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
@Slf4j
public class SmsVerifyService {

    private final SmsVerifyRepository smsVerifyRepository;

    /**
     * 인증번호 발송 (SMS 연동 전 개발모드: 로그에 출력)
     */
    @Transactional
    public String sendCode(String mobilePhone, String purpose) {
        String code = generateCode();

        SmsVerify verify = SmsVerify.builder()
                .mobilePhone(mobilePhone)
                .verifyCode(code)
                .purpose(purpose)
                .build();

        smsVerifyRepository.save(verify);

        // TODO: 실제 SMS 발송 연동 (CoolSMS, NHN Cloud 등)
        log.info("[SMS 개발모드] {} → 인증번호: {}", mobilePhone, code);

        return code; // 개발모드에서만 반환, 운영 시 제거
    }

    /**
     * 인증번호 검증
     */
    @Transactional
    public boolean verifyCode(String mobilePhone, String purpose, String code) {
        SmsVerify verify = smsVerifyRepository
                .findTopByMobilePhoneAndPurposeOrderByCreatedAtDesc(mobilePhone, purpose)
                .orElse(null);

        if (verify == null) return false;
        if (verify.isExpired()) return false;
        if (verify.isVerified()) return false;
        if (!verify.getVerifyCode().equals(code)) return false;

        verify.verify();
        return true;
    }

    /**
     * 인증 완료 여부 확인
     */
    @Transactional(readOnly = true)
    public boolean isVerified(String mobilePhone, String purpose) {
        return smsVerifyRepository
                .findTopByMobilePhoneAndPurposeOrderByCreatedAtDesc(mobilePhone, purpose)
                .map(v -> v.isVerified() && !v.isExpired())
                .orElse(false);
    }

    private String generateCode() {
        SecureRandom random = new SecureRandom();
        return String.format("%06d", random.nextInt(1000000));
    }
}
