package com.khanh.timekeeping.controllers;


import com.khanh.timekeeping.exceptions.ERPRuntimeException;
import com.khanh.timekeeping.responses.DefaultResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ExceptionController {

    @ExceptionHandler(BindException.class)
    public ResponseEntity<DefaultResponse<Object>> handleBindException(BindException e) {
        if (e.hasErrors()) {
            return ResponseEntity.badRequest()
                    .body(DefaultResponse.error(e.getAllErrors().get(0).getDefaultMessage()));
        }
        return ResponseEntity.badRequest().body(DefaultResponse.error("Request không hợp lệ!"));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<DefaultResponse<Object>> handleAccessDeniedException(
            AccessDeniedException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(DefaultResponse.error("Bạn không có quyền thực hiện hành động này!"));
    }

    @ExceptionHandler(ERPRuntimeException.class)
    public ResponseEntity<DefaultResponse<Object>> handleERPRuntimeException(ERPRuntimeException e) {
        return ResponseEntity.ok(DefaultResponse.error(e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<DefaultResponse<Object>> handleException(Exception e) {
        log.error("Có lỗi xảy ra: ", e);
        return ResponseEntity.ok(DefaultResponse.error("Có lỗi xảy ra. Vui lòng liên hệ Admin."));
    }
}

