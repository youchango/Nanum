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
    private final com.nanum.domain.file.service.FileService fileService;

    /**
     * 회원 목록을 조회합니다.
     *
     * @param searchDTO 검색 및 페이징 dto
     * @return 회원 DTO 목록
     */
    public List<MemberDTO> getMemberList(SearchDTO searchDTO) {
        Pageable pageable = PageRequest.of(searchDTO.getPage() - 1,
                searchDTO.getRecordSize());
        List<Member> members = memberRepository.searchMembers(searchDTO, pageable).getContent();

        return members.stream().map(m -> {
            MemberDTO dto = new MemberDTO();
            dto.setMemberCode(m.getMemberCode());
            dto.setMemberId(m.getMemberId());
            dto.setMemberName(m.getMemberName());
            dto.setMobilePhone(m.getMobilePhone());
            dto.setPhone(m.getPhone());
            dto.setZipcode(m.getZipcode());
            dto.setAddress(m.getAddress());
            dto.setAddressDetail(m.getAddressDetail());
            dto.setEmail(m.getEmail());
            dto.setMemberType(m.getMemberType() != null ? m.getMemberType().name() : null);
            dto.setApplyYn(m.getApplyYn());
            dto.setMemo(m.getMemo());
            // createdAt은 MemberDTO에 없으면 추가 필요 (현재 MemberDTO 확인 결과 없음)

            if (m.getMemberType() == MemberType.B) {
                memberBizRepository.findById(m.getMemberCode()).ifPresent(biz -> {
                    dto.setBusinessNumber(biz.getBusinessNumber());
                    dto.setCompanyName(biz.getCompanyName());
                    dto.setCeoName(biz.getCeoName());
                    dto.setBusinessType(biz.getBusinessType());
                    dto.setBusinessItem(biz.getBusinessItem());
                });

                // 사업자 등록증 이미지 (BIZ 타입)
                com.nanum.domain.file.model.FileStore licenseFile = fileService.getMainFile(
                        com.nanum.domain.file.model.ReferenceType.BIZ, m.getMemberCode());
                if (licenseFile != null) {
                    dto.setBusinessLicenseImage(fileService.getFullUrl(licenseFile.getPath()));
                }
            }
            return dto;
        }).toList();
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
            role = MemberRole.ROLE_VETERAN;
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

        // 기업 회원일 경우 MemberBiz 정보 저장
        if (memberType == MemberType.B) {
            MemberBiz memberBiz = MemberBiz.builder()
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

    /**
     * 회원 상세 정보를 조회합니다.
     *
     * @param memberCode 회원 고유 코드
     * @return 회원 DTO
     */
    @Transactional(readOnly = true)
    public MemberDTO getMemberDetail(String memberCode) {
        Member member = memberRepository.findByMemberCode(memberCode)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다. Code: " + memberCode));

        MemberDTO dto = new MemberDTO();
        dto.setMemberCode(member.getMemberCode());
        dto.setMemberId(member.getMemberId());
        dto.setMemberName(member.getMemberName());
        dto.setMobilePhone(member.getMobilePhone());
        dto.setPhone(member.getPhone());
        dto.setZipcode(member.getZipcode());
        dto.setAddress(member.getAddress());
        dto.setAddressDetail(member.getAddressDetail());
        dto.setEmail(member.getEmail());
        dto.setMemberType(member.getMemberType() != null ? member.getMemberType().name() : null);
        dto.setApplyYn(member.getApplyYn());
        dto.setMemo(member.getMemo());

        // 기업 회원인 경우 상세 정보 추가
        if (member.getMemberType() == MemberType.B) {
            memberBizRepository.findById(memberCode).ifPresent(biz -> {
                dto.setBusinessNumber(biz.getBusinessNumber());
                dto.setCompanyName(biz.getCompanyName());
                dto.setCeoName(biz.getCeoName());
                dto.setBusinessType(biz.getBusinessType());
                dto.setBusinessItem(biz.getBusinessItem());
            });

            // 사업자 등록증 이미지 (BIZ 타입)
            com.nanum.domain.file.model.FileStore licenseFile = fileService.getMainFile(
                    com.nanum.domain.file.model.ReferenceType.BIZ, memberCode);
            if (licenseFile != null) {
                dto.setBusinessLicenseImage(fileService.getFullUrl(licenseFile.getPath()));
            }
        }

        return dto;
    }

    @Transactional
    public void updateMember(String memberCode, MemberDTO memberDTO) {
        Member member = memberRepository.findByMemberCode(memberCode)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다. Code: " + memberCode));

        // 기본 정보 업데이트
        if (memberDTO.getMemberName() != null)
            member.setMemberName(memberDTO.getMemberName());
        if (memberDTO.getMobilePhone() != null)
            member.setMobilePhone(memberDTO.getMobilePhone());
        if (memberDTO.getPhone() != null)
            member.setPhone(memberDTO.getPhone());
        if (memberDTO.getZipcode() != null)
            member.setZipcode(memberDTO.getZipcode());
        if (memberDTO.getAddress() != null)
            member.setAddress(memberDTO.getAddress());
        if (memberDTO.getAddressDetail() != null)
            member.setAddressDetail(memberDTO.getAddressDetail());
        if (memberDTO.getEmail() != null)
            member.setEmail(memberDTO.getEmail());

        // 기업 회원 정보 업데이트
        if (member.getMemberType() == MemberType.B) {
            MemberBiz memberBiz = memberBizRepository.findById(memberCode)
                    .orElseGet(() -> MemberBiz.builder().memberCode(member.getMemberCode()).build());

            MemberBiz updatedBiz = MemberBiz.builder()
                    .memberCode(member.getMemberCode())
                    .businessNumber(memberDTO.getBusinessNumber() != null ? memberDTO.getBusinessNumber()
                            : memberBiz.getBusinessNumber())
                    .companyName(memberDTO.getCompanyName() != null ? memberDTO.getCompanyName()
                            : memberBiz.getCompanyName())
                    .ceoName(memberDTO.getCeoName() != null ? memberDTO.getCeoName() : memberBiz.getCeoName())
                    .businessType(memberDTO.getBusinessType() != null ? memberDTO.getBusinessType()
                            : memberBiz.getBusinessType())
                    .businessItem(memberDTO.getBusinessItem() != null ? memberDTO.getBusinessItem()
                            : memberBiz.getBusinessItem())
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

    /**
     * 회원의 승인 상태(apply_yn)를 업데이트합니다.
     *
     * @param memberCode 회원 고유 코드
     * @param applyYn    업데이트할 승인 상태 ('Y' 또는 'N')
     */
    @Transactional
    public void updateApplyYn(String memberCode, String applyYn) {
        Member member = memberRepository.findByMemberCode(memberCode)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다. Code: " + memberCode));
        member.setApplyYn(applyYn);
    }

    /**
     * 회원의 메모를 업데이트합니다.
     *
     * @param memberCode 회원 고유 코드
     * @param memo       업데이트할 메모 내용
     */
    @Transactional
    public void updateMemo(String memberCode, String memo) {
        Member member = memberRepository.findByMemberCode(memberCode)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다. Code: " + memberCode));
        member.setMemo(memo);
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
