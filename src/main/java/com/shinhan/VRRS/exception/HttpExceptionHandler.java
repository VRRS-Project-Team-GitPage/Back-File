package com.shinhan.VRRS.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ControllerAdvice
public class HttpExceptionHandler {
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED) // 405 Method Not Allowed
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public void handleMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        log.warn("Unsupported HTTP Method: {} on URL: {}", ex.getMethod(), request.getRequestURI());
    }

    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE) // 415 Unsupported Media Type
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public void handleMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex, HttpServletRequest request) {
        log.warn("Unsupported Media Type: {} on URL: {}", ex.getContentType(), request.getRequestURI());
    }
}
