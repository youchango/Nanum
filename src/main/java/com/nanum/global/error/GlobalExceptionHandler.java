package com.nanum.global.error;

import com.nanum.global.error.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * ?�역 ?�외 처리 ?�들?�입?�다.
 * 모든 컨트롤러?�서 발생?�는 ?�외�??�아??공통 ?�러 ?�이지�?리다?�렉?�하거나 ?�답??처리?�니??
 */
@org.springframework.web.bind.annotation.RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<com.nanum.global.common.dto.ApiResponse<Void>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("handleMethodArgumentNotValidException", e);
        String message = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return ResponseEntity.badRequest().body(com.nanum.global.common.dto.ApiResponse.error(message));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<com.nanum.global.common.dto.ApiResponse<Void>> handleBusinessException(final BusinessException e) {
        log.error("handleBusinessException", e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(com.nanum.global.common.dto.ApiResponse.error(e.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<com.nanum.global.common.dto.ApiResponse<Void>> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("handleIllegalArgumentException", e);
        return ResponseEntity.badRequest().body(com.nanum.global.common.dto.ApiResponse.error(e.getMessage()));
    }

    @ExceptionHandler(org.springframework.security.authentication.BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<com.nanum.global.common.dto.ApiResponse<Void>> handleBadCredentialsException(org.springframework.security.authentication.BadCredentialsException e) {
        log.error("handleBadCredentialsException", e);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(com.nanum.global.common.dto.ApiResponse.error("?�이???�는 비�?번호가 ?�치?��? ?�습?�다."));
    }

    /**
     * HTTP 메소?��? 지?�되지 ?�는 경우 처리
     * ?�청 URI?� 지?�되??메소?��? 로그??출력?�여 ?�버깅을 ?�이?�게 ??
     */
    @ExceptionHandler(org.springframework.web.HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ResponseEntity<com.nanum.global.common.dto.ApiResponse<Void>> handleHttpRequestMethodNotSupportedException(
            org.springframework.web.HttpRequestMethodNotSupportedException e,
            jakarta.servlet.http.HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        String method = request.getMethod();
        String supportedMethods = e.getSupportedHttpMethods() != null ? 
            String.join(", ", e.getSupportedHttpMethods().stream().map(Object::toString).toList()) : "?�음";
        
        log.error("HTTP 메소??불일�?- URI: {}, ?�청 메소?? {}, 지?�되??메소?? {}", 
                  requestUri, method, supportedMethods, e);
        
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(com.nanum.global.common.dto.ApiResponse.error(
                    String.format("?�청 메소??'%s'??지?�되지 ?�습?�다. 지?�되??메소?? %s", method, supportedMethods)));
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<com.nanum.global.common.dto.ApiResponse<Void>> handleException(Exception e) {
        log.error("handleException", e);
        return ResponseEntity.internalServerError().body(com.nanum.global.common.dto.ApiResponse.error("Internal Server Error: " + e.getMessage())); // Showing message for debug
    }
}
