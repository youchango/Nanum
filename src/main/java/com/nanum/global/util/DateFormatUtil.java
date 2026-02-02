package com.nanum.global.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * JSP EL ?¨ìˆ˜??? ì§œ ?¬ë§· ? í‹¸ë¦¬í‹°
 * JSP?ì„œ LocalDateTime???½ê²Œ ?¬ë§·?????ˆë„ë¡?ì§€??
 */
public class DateFormatUtil {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    
    /**
     * LocalDateTime??? ì§œ ?•ì‹(yyyy-MM-dd)?¼ë¡œ ?¬ë§·
     * @param dateTime LocalDateTime ê°ì²´
     * @return ?¬ë§·??? ì§œ ë¬¸ì??
     */
    public static String formatDate(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        return dateTime.format(DATE_FORMATTER);
    }
    
    /**
     * LocalDateTime??? ì§œ?œê°„ ?•ì‹(yyyy-MM-dd HH:mm)?¼ë¡œ ?¬ë§·
     * @param dateTime LocalDateTime ê°ì²´
     * @return ?¬ë§·??? ì§œ?œê°„ ë¬¸ì??
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        return dateTime.format(DATETIME_FORMATTER);
    }
    
    /**
     * LocalDateTime???œê°„ ?•ì‹(HH:mm)?¼ë¡œ ?¬ë§·
     * @param dateTime LocalDateTime ê°ì²´
     * @return ?¬ë§·???œê°„ ë¬¸ì??
     */
    public static String formatTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        return dateTime.format(TIME_FORMATTER);
    }
    
    /**
     * LocalDateTime??ì»¤ìŠ¤?€ ?¨í„´?¼ë¡œ ?¬ë§·
     * @param dateTime LocalDateTime ê°ì²´
     * @param pattern ? ì§œ ?¬ë§· ?¨í„´
     * @return ?¬ë§·??ë¬¸ì??
     */
    public static String format(LocalDateTime dateTime, String pattern) {
        if (dateTime == null || pattern == null) {
            return "";
        }
        return dateTime.format(DateTimeFormatter.ofPattern(pattern));
    }
}
