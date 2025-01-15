package com.example.userservice.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ResponseStatusException 처리
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatusException(ResponseStatusException ex) {
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("timestamp", LocalDateTime.now());
        errorDetails.put("status", ex.getStatusCode().value());
        errorDetails.put("error", (ex.getStatusCode() instanceof HttpStatus) ?
                ((HttpStatus) ex.getStatusCode()).getReasonPhrase() :
                "Unknown Status");
        errorDetails.put("message", ex.getReason());
        errorDetails.put("path", "/users/orders"); // 요청 경로를 동적으로 변경할 수 있습니다.

        return new ResponseEntity<>(errorDetails, ex.getStatusCode());
    }


    // 일반적인 Exception 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneralException(Exception ex) {
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("timestamp", LocalDateTime.now());
        errorDetails.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorDetails.put("error", "Internal Server Error");
        errorDetails.put("message", ex.getMessage());
        errorDetails.put("path", "/users/orders"); // 요청 경로를 동적으로 설정 가능

        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
