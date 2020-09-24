package com.ekino.team2.punctuality.model;

import lombok.Data;

@Data
public class LoginResponse {
    private String accessToken;

    public LoginResponse(String accessToken) {
        this.accessToken = accessToken;
    }
}
