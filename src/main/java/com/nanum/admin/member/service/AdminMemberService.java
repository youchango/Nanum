package com.nanum.admin.member.service;

import com.nanum.global.common.dto.SearchDTO;
import com.nanum.domain.member.model.Member;
import com.nanum.domain.member.model.MemberBiz;
import com.nanum.domain.member.dto.MemberDTO;
import com.nanum.domain.member.model.MemberRole;
import com.nanum.domain.member.model.MemberType;
import com.nanum.user.member.repository.MemberBizRepository;
import com.nanum.user.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.PageRequest;
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
        Pageable pageable = PageRequest.of(searchDTO.getPage() - 1,
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
        if (memberRepository.existsByMemberId(memberDTO.getMemberId())) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }

        Member member = memberDTO.toEntity();

        // toEntity에서 MemberId는 세팅되었지만, member_code 자동생성 로직이 필요함.
        // 현재 로직상 Auto Increment Logic이 MemberServiceImpl에만 있음.
        // 관리자 생성시에도 동일 로직 적용 필요.

        // ... 생략 (비밀번호 암호화 등)
        member.setPassword(passwordEncoder.encode(memberDTO.getPassword()));

        // 권한 설정 (DTO에 없으면 기본 BIZ)
        // 회원 유형 및 권한 설정 (U: 사용자, B: 업무자, M: 관리자)
        MemberRole role = MemberRole.ROLE_USER;
        MemberType memberType = MemberType.U;

        String typeInput = memberDTO.getMemberType();

        if ("B".equals(typeInput)) {
            role = MemberRole.ROLE_BIZ;
            memberType = MemberType.B;
        } else if ("V".equals(typeInput)) {
            role = MemberRole.ROLE_USER;
            memberType = MemberType.V;
        } else {
            // Default or "U"
            role = MemberRole.ROLE_USER;
            memberType = MemberType.U;
        }

        member.setRole(role);
        member.setMemberType(memberType);

        // Member Code 생성
        String prefix = (memberType == MemberType.B) ? "CB" : "MB";
        // generateMemberCode 복제 필요 혹은 서비스 분리 필요. 일단 복제 (Repository 호출)
        String memberCode = generateMemberCode(prefix); // 아래 private 메서드 추가 필요
        member.setMemberCode(memberCode);

        memberRepository.save(member);

        // ... (MemberBiz 저장 로직)
    }

    // ...

    @Transactional(readOnly = true)
    public Member getMember(String memberCode) {
        return memberRepository.findByMemberCode(memberCode)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다. Code: " + memberCode));
    }

    @Transactional
    public void updateMember(String memberCode, MemberDTO memberDTO) {
        Member member = memberRepository.findByMemberCode(memberCode)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다. Code: " + memberCode));
        // ... (이하 동일)

        // MemberBiz 조회도 memberCode 사용 (MemberBizRepository PK가 String memberCode라면
        // findById 사용 가능)
        if (member.getMemberType() == MemberType.B && memberDTO.getBusinessNumber() != null) {
            MemberBiz memberBiz = memberBizRepository.findById(memberCode)
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

        // Role/Type update if provided
        if (memberDTO.getMemberType() != null && !memberDTO.getMemberType().isEmpty()) {
            String typeInput = memberDTO.getMemberType();
            if ("B".equals(typeInput)) {
                member.setRole(MemberRole.ROLE_BIZ);
                member.setMemberType(MemberType.B);
            } else if ("V".equals(typeInput)) {
                member.setRole(MemberRole.ROLE_VETERAN);
                member.setMemberType(MemberType.V);
            } else if ("U".equals(typeInput)) {
                member.setRole(MemberRole.ROLE_USER);
                member.setMemberType(MemberType.U);
            }
        }

        // Update applyYn
        if (memberDTO.getApplyYn() != null && !memberDTO.getApplyYn().isEmpty()) {
            member.setApplyYn(memberDTO.getApplyYn());
        }

        // Update memo
        if (memberDTO.getMemo() != null) {
            member.setMemo(memberDTO.getMemo());
        }
    }

    /**
     * 회원을 탈퇴(Soft Delete) 처리합니다.
     *
     * @param memberCode 회원 고유 코드
     */
    @Transactional
    public void deleteMember(String memberCode) {
        Member member = memberRepository.findByMemberCode(memberCode)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다. Code: " + memberCode));
        member.setWithdrawYn("Y");
    }

    // Member Code 생성 helper
    private String generateMemberCode(String prefix) {
        return memberRepository.findTopByMemberCodeStartingWithOrderByMemberCodeDesc(prefix)
                .map(m -> {
                    String lastCode = m.getMemberCode();
                    String numberPart = lastCode.substring(prefix.length());
                    try {
                        long number = Long.parseLong(numberPart);
                        return String.format("%s%06d", prefix, number + 1);
                    } catch (NumberFormatException e) {
                        return prefix + "000001";
                    }
                })
                .orElse(prefix + "000001");
    }
}
