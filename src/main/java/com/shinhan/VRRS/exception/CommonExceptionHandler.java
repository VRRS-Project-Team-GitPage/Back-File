package com.shinhan.VRRS.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.NoSuchElementException;

@ControllerAdvice
public class CommonExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValid() {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // 400 Bad Request
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument() {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // 400 Bad Request
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<String> handleUserNotFound() {
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED); // 401 Unauthorized
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> handleNoSuchElement() {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 Not Found
    }
}