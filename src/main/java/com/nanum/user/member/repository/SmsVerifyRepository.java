package com.nanum.user.member.repository;

import com.nanum.domain.member.model.SmsVerify;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SmsVerifyRepository extends JpaRepository<SmsVerify, Long> {

    Optional<SmsVerify> findTopByMobilePhoneAndPurposeOrderByCreatedAtDesc(String mobilePhone, String purpose);

    Optional<SmsVerify> findTopByTargetAndPurposeOrderByCreatedAtDesc(String target, String purpose);
}
