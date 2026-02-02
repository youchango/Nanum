package com.nanum.admin.member.service;

import com.nanum.global.common.dto.SearchDTO;
import com.nanum.user.member.model.Member;
import com.nanum.user.member.model.MemberBiz;
import com.nanum.user.member.model.MemberDTO;
import com.nanum.user.member.model.MemberRole;
import com.nanum.user.member.model.MemberType;
import com.nanum.user.member.repository.MemberBizRepository;
import com.nanum.user.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Pageable;
import java.util.List;

/**
 * 관리자 회원 관리 서비스입니다.
 */
@Service
@RequiredArgsConstructor
public class AdminMemberService {

    // private final MemberMapper memberMapper; // QueryDSL 전환으로 제거
    private final MemberRepository memberRepository;
    private final MemberBizRepository memberBizRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 회원 목록을 조회합니다.
     *
     * @param searchDTO 검색 및 페이징 dto
     * @return 회원 목록 리스트
     */
    public List<Member> getMemberList(SearchDTO searchDTO) {
        Pageable pageable = org.springframework.data.domain.PageRequest.of(searchDTO.getPage() - 1,
                searchDTO.getRecordSize());
        return memberRepository.searchMembers(searchDTO, pageable).getContent();
    }

    /**
     * 회원 수를 조회합니다.
     *
     * @param searchDTO 검색 조건
     * @return 회원 수
     */
    public int getMemberCount(SearchDTO searchDTO) {
        Pageable pageable = org.springframework.data.domain.PageRequest.of(searchDTO.getPage() - 1,
                searchDTO.getRecordSize());
        return (int) memberRepository.searchMembers(searchDTO, pageable).getTotalElements();
    }

    /**
     * 회원을 생성합니다.
     *
     * @param memberDTO 회원 가입 정보
     */
    @Transactional
    public void createMember(MemberDTO memberDTO) {
        // 아이디 중복 체크
        if (memberRepository.existsByMemberLogin(memberDTO.getMemberLogin())) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }

        Member member = memberDTO.toEntity();
        member.setPassword(passwordEncoder.encode(memberDTO.getPassword()));

        // 권한 설정 (DTO에 없으면 기본 BIZ)
        MemberRole role = MemberRole.ROLE_BIZ;
        if (memberDTO.getRole() != null && !memberDTO.getRole().isEmpty()) {
            try {
                role = MemberRole.valueOf(memberDTO.getRole());
            } catch (IllegalArgumentException e) {
                // Invalid role, keep default or throw
            }
        }
        member.setRole(role);

        // 타입 설정
        if (role == MemberRole.ROLE_MASTER) {
            member.setMemberType(MemberType.ADMIN);
        } else if (role == MemberRole.ROLE_USER) {
            member.setMemberType(MemberType.USER);
        } else {
            member.setMemberType(MemberType.BIZ);
        }

        memberRepository.save(member);

        // 기업회원(BIZ)인 경우 MemberBiz 상세 정보 저장
        if (member.getMemberType() == MemberType.BIZ) {
            MemberBiz memberBiz = MemberBiz.builder()
                    .member(member)
                    .businessNumber(memberDTO.getBusinessNumber())
                    .companyName(memberDTO.getCompanyName() != null ? memberDTO.getCompanyName() : "미등록")
                    .ceoName(memberDTO.getCeoName() != null ? memberDTO.getCeoName() : "미등록")
                    .build();
            memberBizRepository.save(memberBiz);
        }
    }

    /**
     * 회원 상세 정보를 조회합니다.
     *
     * @param memberId 회원 ID
     * @return 회원 엔티티
     */
    @Transactional(readOnly = true)
    public Member getMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다. ID: " + memberId));
    }

    /**
     * 회원 정보를 수정합니다.
     *
     * @param memberId  회원 ID
     * @param memberDTO 수정할 정보
     */
    @Transactional
    public void updateMember(Long memberId, MemberDTO memberDTO) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다. ID: " + memberId));

        // Update fields
        if (memberDTO.getMemberName() != null)
            member.setMemberName(memberDTO.getMemberName());
        if (memberDTO.getMobilePhone() != null)
            member.setMobilePhone(memberDTO.getMobilePhone());
        if (memberDTO.getEmail() != null)
            member.setEmail(memberDTO.getEmail());
        if (memberDTO.getZipcode() != null)
            member.setZipcode(memberDTO.getZipcode());
        if (memberDTO.getAddress() != null)
            member.setAddress(memberDTO.getAddress());
        if (memberDTO.getAddressDetail() != null)
            member.setAddressDetail(memberDTO.getAddressDetail());

        // Business Number update via MemberBiz
        if (member.getMemberType() == MemberType.BIZ && memberDTO.getBusinessNumber() != null) {
            MemberBiz memberBiz = memberBizRepository.findById(memberId)
                    .orElseGet(() -> MemberBiz.builder().member(member).build());
            // 기업회원 상세 정보 업데이트 (MemberBiz 엔티티에 업데이트 메서드 추가 권장)
            // 여기서는 빌더 패턴이나 직접 할당 사용
            MemberBiz updatedBiz = MemberBiz.builder()
                    .member(member)
                    .businessNumber(memberDTO.getBusinessNumber())
                    .companyName(memberDTO.getCompanyName() != null ? memberDTO.getCompanyName()
                            : memberBiz.getCompanyName())
                    .ceoName(memberDTO.getCeoName() != null ? memberDTO.getCeoName() : memberBiz.getCeoName())
                    .build();
            memberBizRepository.save(updatedBiz);
        }

        // Password update if provided
        if (memberDTO.getPassword() != null && !memberDTO.getPassword().isEmpty()) {
            member.setPassword(passwordEncoder.encode(memberDTO.getPassword()));
        }

        // Role update if provided
        if (memberDTO.getRole() != null && !memberDTO.getRole().isEmpty()) {
            try {
                MemberRole role = MemberRole.valueOf(memberDTO.getRole());
                member.setRole(role);
                // Update Type accordingly
                if (role == MemberRole.ROLE_MASTER)
                    member.setMemberType(MemberType.ADMIN);
                else if (role == MemberRole.ROLE_USER)
                    member.setMemberType(MemberType.USER);
                else
                    member.setMemberType(MemberType.BIZ);
            } catch (IllegalArgumentException e) {
                // Ignore invalid role
            }
        }
    }
}
