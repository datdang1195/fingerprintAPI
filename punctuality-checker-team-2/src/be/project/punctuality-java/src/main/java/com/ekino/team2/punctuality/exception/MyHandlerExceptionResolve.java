package com.ekino.team2.punctuality.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class MyHandlerExceptionResolve extends ResponseEntityExceptionHandler {

    @ExceptionHandler(MyException.class)
    public ResponseEntity<Object> loginError(MyException exp) {
        ApiError apiError = ApiError.builder().errorCode(exp.getErrorCode()).message(exp.getErrorMsg()).build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
    }

}