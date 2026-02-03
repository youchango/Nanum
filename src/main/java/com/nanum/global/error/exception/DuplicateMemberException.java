package com.nanum.global.error.exception;

import com.nanum.global.error.ErrorCode;

/**
 * ?�원가?????�이??중복 ?�으�??�해 ?�원???�록?????�을 ??발생?�는 ?�외?�니??
 */
public class DuplicateMemberException extends BusinessException {

    /**
     * @param value 중복???�원 ID �?
     */
    public DuplicateMemberException(String value) {
        super(value, ErrorCode.DUPLICATE_MEMBER);
    }
}
