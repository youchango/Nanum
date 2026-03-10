package com.nanum.global.common.support;

import com.nanum.global.common.dto.ApiResponse;
import org.springframework.http.ResponseEntity;

/**
 * 컨트롤러에서 공통적으로 사용하는 응답 생성을 지원하는 인터페이스
 */
public interface ResponseSupport {

    /**
     * 성공 응답을 생성합니다.
     *
     * @param data 반환할 데이터
     * @param <T>  데이터 타입
     * @return ResponseEntity<ApiResponse<T>>
     */
    default <T> ResponseEntity<ApiResponse<T>> success(T data) {
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    /**
     * 데이터 없이 성공 응답을 생성합니다. (success() 호출 호환용)
     *
     * @param <T> 데이터 타입
     * @return ResponseEntity<ApiResponse<T>>
     */
    default <T> ResponseEntity<ApiResponse<T>> success() {
        return ok();
    }

    /**
     * 데이터가 없는 성공 응답을 생성합니다.
     *
     * @param <T> 데이터 타입 (Void)
     * @return ResponseEntity<ApiResponse<T>>
     */
    default <T> ResponseEntity<ApiResponse<T>> ok() {
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
