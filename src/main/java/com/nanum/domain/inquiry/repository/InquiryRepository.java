package com.nanum.domain.inquiry.repository;

import com.nanum.domain.inquiry.model.Inquiry;
import com.nanum.domain.member.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InquiryRepository extends JpaRepository<Inquiry, Long>, InquiryRepositoryCustom {
    List<Inquiry> findByWriter(Member writer);

    List<Inquiry> findTop5BySiteCdOrderByCreatedAtDesc(String siteCd);

    List<Inquiry> findTop5ByOrderByCreatedAtDesc();
}
