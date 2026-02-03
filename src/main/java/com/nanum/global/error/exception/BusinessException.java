package com.nanum.global.error.exception;

import com.nanum.global.error.ErrorCode;
import lombok.Getter;

/**
 * 비즈?�스 로직 ?�행 �?발생?�는 ?�외???�위 ?�래?�입?�다.
 * RuntimeException???�속받아 Unchecked Exception?�로 ?�작?�니??
 * ?�러 코드(ErrorCode)�??�함?�여 구체?�인 ?�외 ?�황???�달?�니??
 */
@Getter
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;

    /**
     * @param message ?�외 ?�세 메시지
     * @param errorCode ?�러 코드 Enum
     */
    public BusinessException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * @param errorCode ?�러 코드 Enum (메시지??ErrorCode??기본 메시지 ?�용)
     */
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
