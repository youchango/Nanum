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
        if (memberRepository.existsByMemberLogin(memberDTO.getMemberLogin())) {
            throw new com.nanum.global.error.exception.DuplicateMemberException(memberDTO.getMemberLogin());
        }

        Member member = new Member();
        member.setMemberName(memberDTO.getMemberName());
        member.setMemberLogin(memberDTO.getMemberLogin());

        // 비밀번호 암호화 (BCrypt)
        member.setPassword(passwordEncoder.encode(memberDTO.getPassword()));

        member.setMobilePhone(memberDTO.getMobilePhone());
        member.setZipcode(memberDTO.getZipcode());
        member.setAddress(memberDTO.getAddress());
        member.setAddressDetail(memberDTO.getAddressDetail());
        member.setEmail(memberDTO.getEmail());

        // 사용자(User) 권한 및 타입 설정
        member.setRole(MemberRole.ROLE_USER);
        member.setMemberType(MemberType.USER);

        memberRepository.save(member);
    }
}
