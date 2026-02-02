package com.nanum.global.error.exception;

import com.nanum.global.error.ErrorCode;

/**
 * ?μ›κ°€?????„μ΄??μ¤‘λ³µ ?±μΌλ΅??Έν•΄ ?μ›???±λ΅?????†μ„ ??λ°μƒ?λ” ?μ™Έ?…λ‹??
 */
public class DuplicateMemberException extends BusinessException {

    /**
     * @param value μ¤‘λ³µ???μ› ID κ°?
     */
    public DuplicateMemberException(String value) {
        super(value, ErrorCode.DUPLICATE_MEMBER);
    }
}
