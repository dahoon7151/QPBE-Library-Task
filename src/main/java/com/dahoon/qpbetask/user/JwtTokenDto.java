package com.dahoon.qpbetask.user;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class JwtTokenDto {
    private String grantType;

    private String accessToken;

    private String refreshToken;

    public JwtTokenDto(String accessToken, String refreshToken) {
        this.grantType = "Bearer";
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
