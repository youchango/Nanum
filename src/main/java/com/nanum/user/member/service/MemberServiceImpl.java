package com.nanum.user.member.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nanum.domain.member.model.Member;
import com.nanum.domain.member.dto.MemberDTO;
import com.nanum.domain.member.dto.PasswordResetRequest;
import com.nanum.domain.member.dto.PasswordResetResponse;
import com.nanum.domain.member.dto.ProfileResponse;
import com.nanum.domain.member.dto.ProfileUpdateRequest;
import com.nanum.domain.member.model.MemberRole;
import com.nanum.domain.member.model.MemberType;
import com.nanum.user.member.repository.MemberRepository;

/**
 * 회원 관련 비즈니스 로직을 처리하는 서비스 구현체입니다.
 */
@Service
public class MemberServiceImpl implements MemberService {

    // private final MemberMapper memberMapper; // 제거
    private final MemberRepository memberRepository;
    private final com.nanum.user.member.repository.MemberBizRepository memberBizRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberServiceImpl(MemberRepository memberRepository,
            com.nanum.user.member.repository.MemberBizRepository memberBizRepository,
            PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.memberBizRepository = memberBizRepository;
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

        // 이메일 중복 체크
        if (memberDTO.getEmail() != null && !memberDTO.getEmail().isBlank()
                && memberRepository.existsByEmail(memberDTO.getEmail())) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
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

        // MemberCode 생성 (Auto Increment Logic)
        String prefix = (member.getMemberType() == MemberType.B) ? "CB" : "MB";
        String memberCode = generateMemberCode(prefix);
        member.setMemberCode(memberCode);

        // createdBy, updatedBy 등은 JPA Auditing 혹은 여기서 직접 설정
        // member.setCreatedBy(memberCode); // 자기 자신이 생성
        // member.setUpdatedBy(memberCode);

        memberRepository.save(member);

        // 기업 회원일 경우 상세 정보 저장
        if (memberType == MemberType.B) {
            com.nanum.domain.member.model.MemberBiz memberBiz = com.nanum.domain.member.model.MemberBiz.builder()
                    .memberCode(member.getMemberCode())
                    .businessNumber(memberDTO.getBusinessNumber())
                    .companyName(memberDTO.getCompanyName())
                    .ceoName(memberDTO.getCeoName())
                    .businessType(memberDTO.getBusinessType())
                    .businessItem(memberDTO.getBusinessItem())
                    .build();
            memberBizRepository.save(memberBiz);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public ProfileResponse getProfile(String memberId) {
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다: " + memberId));

        if (member.getMemberType() == MemberType.B) {
            com.nanum.domain.member.model.MemberBiz biz = memberBizRepository.findById(member.getMemberCode()).orElse(null);
            return ProfileResponse.from(member, biz);
        }
        return ProfileResponse.from(member);
    }

    @Transactional
    @Override
    public void updateProfile(String memberId, ProfileUpdateRequest request) {
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다: " + memberId));

        member.setMemberName(request.getMemberName());
        member.setMobilePhone(request.getMobilePhone());
        member.setEmail(request.getEmail());
        member.setZipcode(request.getZipcode());
        member.setAddress(request.getAddress());
        member.setAddressDetail(request.getAddressDetail());
        member.setSmsYn(request.getSmsYn());
        member.setEmailYn(request.getEmailYn());

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            // 기존 비밀번호 검증 필수
            if (request.getCurrentPassword() == null || request.getCurrentPassword().isBlank()) {
                throw new IllegalArgumentException("비밀번호 변경 시 기존 비밀번호를 입력해주세요.");
            }
            if (!passwordEncoder.matches(request.getCurrentPassword(), member.getPassword())) {
                throw new IllegalArgumentException("기존 비밀번호가 일치하지 않습니다.");
            }
            member.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        memberRepository.save(member);
    }

    @Transactional
    @Override
    public void withdraw(String memberId, String password) {
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("비밀번호를 입력해주세요.");
        }
        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        member.setWithdrawYn("Y");
        member.setWithdrawAt(java.time.LocalDateTime.now());
        memberRepository.save(member);
    }

    @Transactional
    @Override
    public PasswordResetResponse resetPassword(PasswordResetRequest request) {
        Member member = memberRepository.findByMemberId(request.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

        // 본인 확인 (이름 + 이메일)
        if (!member.getMemberName().equals(request.getMemberName()) ||
                !request.getEmail().equals(member.getEmail())) {
            throw new IllegalArgumentException("입력하신 정보가 일치하지 않습니다.");
        }

        // 탈퇴 회원 체크
        if ("Y".equals(member.getWithdrawYn())) {
            throw new IllegalArgumentException("탈퇴한 회원입니다.");
        }

        // 임시 비밀번호 생성 (8자리: 영문+숫자)
        String tempPassword = generateTempPassword();

        // 비밀번호 업데이트
        member.setPassword(passwordEncoder.encode(tempPassword));
        memberRepository.save(member);

        return new PasswordResetResponse(member.getMemberId(), tempPassword);
    }

    private String generateTempPassword() {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789";
        StringBuilder sb = new StringBuilder();
        java.security.SecureRandom random = new java.security.SecureRandom();
        for (int i = 0; i < 8; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
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
