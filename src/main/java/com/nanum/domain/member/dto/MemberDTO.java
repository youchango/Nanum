package com.nanum.domain.member.dto;

import com.nanum.domain.member.model.Member;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 회원가입 등 회원 관련 데이터 전송 객체(DTO)입니다.
 * 입력값에 대한 유효성 검증(Validation) 어노테이션이 적용되어 있습니다.
 */
@Data
public class MemberDTO {

    /**
     * 회원의 실명입니다. 필수 입력 항목입니다.
     */
    @NotBlank(message = "이름은 필수입니다.")
    private String memberName;

    /**
     * 회원 식별용 고유 코드 (Natural Key)
     */
    private String memberCode;

    /**
     * 로그인에 사용될 아이디입니다.
     * 영문 소문자와 숫자 조합, 4~20자로 제한됩니다.
     */
    @NotBlank(message = "아이디는 필수입니다.")
    @Size(min = 4, max = 20, message = "아이디는 4~20자 사이여야 합니다.")
    @Pattern(regexp = "^[a-z0-9]*$", message = "아이디는 영문 소문자와 숫자만 사용 가능합니다.")
    private String memberId;

    /**
     * 비밀번호입니다. 최소 4자 이상이어야 합니다.
     * 실제 저장 시에는 암호화되어 DB에 저장됩니다.
     */
    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 4, message = "비밀번호는 4자 이상이어야 합니다.")
    private String password;

    /**
     * 휴대전화번호입니다. 지정된 형식(010-XXXX-XXXX)을 따라야 합니다.
     */
    @NotBlank(message = "휴대전화번호는 필수입니다.")
    @Pattern(regexp = "^01(?:0|1|[6-9])-(?:\\d{3}|\\d{4})-\\d{4}$", message = "올바른 휴대전화번호 형식이 아닙니다. (예: 010-1234-5678)")
    private String mobilePhone;

    /**
     * 우편번호입니다.
     */
    @NotBlank(message = "우편번호는 필수입니다.")
    private String zipcode;

    /**
     * 기본 주소입니다.
     */
    @NotBlank(message = "주소는 필수입니다.")
    private String address;

    /**
     * 상세 주소입니다.
     */
    @NotBlank(message = "상세주소는 필수입니다.")
    private String addressDetail;

    /**
     * 사업자 등록번호 (Biz 회원용)
     */
    private String businessNumber;

    /**
     * 상호명 (Biz 회원용)
     */
    private String companyName;

    /**
     * 대표자명 (Biz 회원용)
     */
    private String ceoName;

    /**
     * 업태 (Biz 회원용)
     */
    private String businessType;

    /**
     * 종목 (Biz 회원용)
     */
    private String businessItem;

    /**
     * 일반 전화번호
     */
    private String phone;

    /**
     * 이메일 주소입니다. 선택 입력 항목입니다.
     */
    private String email;

    /**
     * 회원 유형 (U: 사용자, B: 업무자, V: 보훈)
     */
    private String memberType;

    /**
     * 승인 여부 (Y/N)
     */
    private String applyYn;

    /**
     * 관리자 메모
     */
    private String memo;

    /**
     * DTO를 Member 엔티티로 변환합니다.
     *
     * @return Member 엔티티
     */
    public Member toEntity() {
        Member member = new Member();
        member.setMemberName(this.memberName);
        member.setMemberId(this.memberId);
        member.setPassword(this.password);
        member.setMobilePhone(this.mobilePhone);
        member.setPhone(this.phone);
        member.setZipcode(this.zipcode);
        member.setAddress(this.address);
        member.setAddressDetail(this.addressDetail);
        member.setEmail(this.email);
        // role, memberType 등은 서비스에서 설정
        return member;
    }
}
