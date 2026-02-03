package com.nanum.global.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 전역적으로 사용되는 에러 코드를 정의하는 Enum 클래스입니다.
 * HTTP 상태 코드, 에러 코드(시스템 내부 식별자), 에러 메시지를 포함합니다.
 */
@AllArgsConstructor
@Getter
public enum ErrorCode {

    // Common (공통 에러)
    INTERNAL_SERVER_ERROR(500, "C001", "Internal Server Error"), // 서버 내부 오류
    INVALID_INPUT_VALUE(400, "C002", "Invalid Input Value"), // 입력값 유효성 검증 실패
    METHOD_NOT_ALLOWED(405, "C003", "Method Not Allowed"), // 지원하지 않는 HTTP 메서드
    ACCESS_DENIED(403, "C004", "Access is Denied"), // 접근 권한 부족
    ENTITY_NOT_FOUND(400, "C005", "Entity Not Found"), // 엔티티를 찾을 수 없음

    // Member (회원 관련 에러)
    MEMBER_NOT_FOUND(404, "M001", "Member Not Found"), // 회원을 찾을 수 없음
    DUPLICATE_MEMBER(409, "M002", "Member Already Exists"), // 이미 존재하는 회원 ID
    EMAIL_DUPLICATION(409, "M003", "Email is Already Exists"), // 이미 존재하는 이메일
    LOGIN_INPUT_INVALID(400, "M004", "Login Input is Invalid"); // 로그인 입력값 오류

    private final int status; // HTTP 상태 코드
    private final String code; // 에러 코드 (예: C001, M001)
    private final String message; // 에러 메시지
}
