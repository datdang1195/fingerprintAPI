package com.ekino.team2.punctuality.exception;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApiError {
    private int errorCode;
    private String message;

}
