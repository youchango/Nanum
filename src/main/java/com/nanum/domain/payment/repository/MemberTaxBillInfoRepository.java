package com.nanum.domain.payment.repository;

import com.nanum.domain.payment.model.MemberTaxBillInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberTaxBillInfoRepository extends JpaRepository<MemberTaxBillInfo, Long> {

    Optional<MemberTaxBillInfo> findByMemberMemberCodeAndInfoType(String memberCode, String infoType);

    List<MemberTaxBillInfo> findAllByMemberMemberCode(String memberCode);

    List<MemberTaxBillInfo> findAllByMemberMemberCodeAndInfoTypeOrderByCreatedAtDesc(String memberCode, String infoType);

    Optional<MemberTaxBillInfo> findByIdAndMemberMemberCode(Long id, String memberCode);
}
