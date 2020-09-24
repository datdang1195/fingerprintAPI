package com.ekino.team2.punctuality.exception;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MyException extends RuntimeException {
    private int errorCode;
    private String errorMsg;
    private String detailMsg;
}
