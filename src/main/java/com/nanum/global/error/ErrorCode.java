package com.nanum.global.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * ?„ì—­?ìœ¼ë¡??¬ìš©?˜ëŠ” ?ëŸ¬ ì½”ë“œë¥??•ì˜?˜ëŠ” Enum ?´ë˜?¤ì…?ˆë‹¤.
 * HTTP ?íƒœ ì½”ë“œ, ?ëŸ¬ ì½”ë“œ(?œìŠ¤???´ë? ?ë³„??, ?ëŸ¬ ë©”ì‹œì§€ë¥??¬í•¨?©ë‹ˆ??
 */
@AllArgsConstructor
@Getter
public enum ErrorCode {

    // Common (ê³µí†µ ?ëŸ¬)
    INTERNAL_SERVER_ERROR(500, "C001", "Internal Server Error"), // ?œë²„ ?´ë? ?¤ë¥˜
    INVALID_INPUT_VALUE(400, "C002", "Invalid Input Value"),     // ?…ë ¥ê°?? íš¨??ê²€ì¦??¤íŒ¨
    METHOD_NOT_ALLOWED(405, "C003", "Method Not Allowed"),       // ì§€?í•˜ì§€ ?ŠëŠ” HTTP ë©”ì„œ??
    ACCESS_DENIED(403, "C004", "Access is Denied"),              // ?‘ê·¼ ê¶Œí•œ ë¶€ì¡?

    // Member (?Œì› ê´€???ëŸ¬)
    MEMBER_NOT_FOUND(404, "M001", "Member Not Found"),           // ?Œì›??ì°¾ì„ ???†ìŒ
    DUPLICATE_MEMBER(409, "M002", "Member Already Exists"),      // ?´ë? ì¡´ì¬?˜ëŠ” ?Œì› ID
    EMAIL_DUPLICATION(409, "M003", "Email is Already Exists"),   // ?´ë? ì¡´ì¬?˜ëŠ” ?´ë©”??
    LOGIN_INPUT_INVALID(400, "M004", "Login Input is Invalid");  // ë¡œê·¸???…ë ¥ê°??¤ë¥˜

    private final int status;   // HTTP ?íƒœ ì½”ë“œ
    private final String code;  // ?ëŸ¬ ì½”ë“œ (?? C001, M001)
    private final String message; // ?ëŸ¬ ë©”ì‹œì§€
}
