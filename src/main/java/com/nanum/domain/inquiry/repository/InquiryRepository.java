package com.nanum.domain.inquiry.repository;

import com.nanum.domain.inquiry.model.Inquiry;
import com.nanum.domain.member.model.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InquiryRepository extends JpaRepository<Inquiry, Long>, InquiryRepositoryCustom {
    List<Inquiry> findByWriter(Member writer);

    Page<Inquiry> findByWriterAndDeleteYnOrderByCreatedAtDesc(Member writer, String deleteYn, Pageable pageable);

    List<Inquiry> findTop5BySiteCdOrderByCreatedAtDesc(String siteCd);

    List<Inquiry> findTop5ByOrderByCreatedAtDesc();

    // 상품 문의 - 공개글 조회 (비밀글 제외)
    Page<Inquiry> findByProductIdAndSiteCdAndDeleteYnAndIsSecretOrderByCreatedAtDesc(Long productId, String siteCd, String deleteYn, String isSecret, Pageable pageable);

    // 상품 문의 - 전체 (사이트별)
    Page<Inquiry> findByProductIdAndSiteCdAndDeleteYnOrderByCreatedAtDesc(Long productId, String siteCd, String deleteYn, Pageable pageable);

    // 상품 문의 - 공개글 조회 (비밀글 제외, 사이트 미지정 - 하위 호환용)
    Page<Inquiry> findByProductIdAndDeleteYnAndIsSecretOrderByCreatedAtDesc(Long productId, String deleteYn, String isSecret, Pageable pageable);

    // 상품 문의 - 전체 (사이티 미지정 - 하위 호환용)
    Page<Inquiry> findByProductIdAndDeleteYnOrderByCreatedAtDesc(Long productId, String deleteYn, Pageable pageable);
}
