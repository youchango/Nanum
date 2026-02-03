package com.nanum.global.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * JSP EL ?�수???�짜 ?�맷 ?�틸리티
 * JSP?�서 LocalDateTime???�게 ?�맷?????�도�?지??
 */
public class DateFormatUtil {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    
    /**
     * LocalDateTime???�짜 ?�식(yyyy-MM-dd)?�로 ?�맷
     * @param dateTime LocalDateTime 객체
     * @return ?�맷???�짜 문자??
     */
    public static String formatDate(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        return dateTime.format(DATE_FORMATTER);
    }
    
    /**
     * LocalDateTime???�짜?�간 ?�식(yyyy-MM-dd HH:mm)?�로 ?�맷
     * @param dateTime LocalDateTime 객체
     * @return ?�맷???�짜?�간 문자??
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        return dateTime.format(DATETIME_FORMATTER);
    }
    
    /**
     * LocalDateTime???�간 ?�식(HH:mm)?�로 ?�맷
     * @param dateTime LocalDateTime 객체
     * @return ?�맷???�간 문자??
     */
    public static String formatTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        return dateTime.format(TIME_FORMATTER);
    }
    
    /**
     * LocalDateTime??커스?� ?�턴?�로 ?�맷
     * @param dateTime LocalDateTime 객체
     * @param pattern ?�짜 ?�맷 ?�턴
     * @return ?�맷??문자??
     */
    public static String format(LocalDateTime dateTime, String pattern) {
        if (dateTime == null || pattern == null) {
            return "";
        }
        return dateTime.format(DateTimeFormatter.ofPattern(pattern));
    }
}
