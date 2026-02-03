package com.nanum.user.member.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nanum.user.member.model.Member;
import com.nanum.user.member.model.MemberDTO;
import com.nanum.user.member.model.MemberRole;
import com.nanum.user.member.model.MemberType;
import com.nanum.user.member.repository.MemberRepository;

/**
 * 회원 관련 비즈니스 로직을 처리하는 서비스 구현체입니다.
 */
@Service
public class MemberServiceImpl implements MemberService {

    // private final MemberMapper memberMapper; // 제거
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberServiceImpl(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 회원가입을 처리합니다.
     * 1. 아이디 중복 검사를 수행합니다.
     * 2. 비밀번호를 암호화합니다.
     * 3. 회원 정보를 DB에 저장합니다.
     *
     * @param memberDTO 가입할 회원 정보가 담긴 DTO
     * @throws com.nanum.global.error.exception.DuplicateMemberException 이미 존재하는
     *                                                                   아이디인 경우 발생
     */
    @Transactional
    @Override
    public void signup(MemberDTO memberDTO) {
        // 아이디 중복 체크
        if (memberRepository.existsByMemberId(memberDTO.getMemberId())) {
            throw new com.nanum.global.error.exception.DuplicateMemberException(memberDTO.getMemberId());
        }

        Member member = new Member();
        member.setMemberName(memberDTO.getMemberName());
        member.setMemberId(memberDTO.getMemberId());

        // 비밀번호 암호화 (BCrypt)
        member.setPassword(passwordEncoder.encode(memberDTO.getPassword()));

        member.setMobilePhone(memberDTO.getMobilePhone());
        member.setZipcode(memberDTO.getZipcode());
        member.setAddress(memberDTO.getAddress());
        member.setAddressDetail(memberDTO.getAddressDetail());
        member.setEmail(memberDTO.getEmail());

        // Role 설정 (DTO에 있으면 사용, 없으면 기본값 User)
        MemberRole role = MemberRole.ROLE_USER;
        if (memberDTO.getRole() != null && !memberDTO.getRole().isEmpty()) {
            try {
                role = MemberRole.valueOf(memberDTO.getRole());
            } catch (IllegalArgumentException e) {
                // Invalid role, keep default
            }
        }
        member.setRole(role);

        // MemberType 설정
        if (role == MemberRole.ROLE_BIZ) {
            member.setMemberType(MemberType.BIZ);
        } else if (role == MemberRole.ROLE_MASTER) {
            member.setMemberType(MemberType.ADMIN);
        } else {
            member.setMemberType(MemberType.USER);
        }

        // MemberCode 생성 (Auto Increment Logic)
        String prefix = (member.getMemberType() == MemberType.BIZ) ? "CB" : "MB";
        String memberCode = generateMemberCode(prefix);
        member.setMemberCode(memberCode);

        // createdBy, updatedBy 등은 JPA Auditing 혹은 여기서 직접 설정
        // member.setCreatedBy(memberCode); // 자기 자신이 생성
        // member.setUpdatedBy(memberCode);

        memberRepository.save(member);
    }

    private String generateMemberCode(String prefix) {
        // 마지막 코드 조회 (예: MB000001)
        return memberRepository.findTopByMemberCodeStartingWithOrderByMemberCodeDesc(prefix)
                .map(m -> {
                    String lastCode = m.getMemberCode();
                    String numberPart = lastCode.substring(prefix.length());
                    try {
                        long number = Long.parseLong(numberPart);
                        // 숫자 증가 및 포맷팅 (000001 -> 6자리)
                        return String.format("%s%06d", prefix, number + 1);
                    } catch (NumberFormatException e) {
                        // 파싱 실패 시 기본값 (기존 데이터가 잘못되었을 경우)
                        return prefix + "000001";
                    }
                })
                .orElse(prefix + "000001"); // 데이터가 없으면 최초 생성
    }
}
