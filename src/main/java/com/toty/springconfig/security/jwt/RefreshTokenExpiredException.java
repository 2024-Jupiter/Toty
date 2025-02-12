package com.toty.springconfig.security.jwt;

import org.springframework.security.core.AuthenticationException;

public class RefreshTokenExpiredException extends AuthenticationException {
    public RefreshTokenExpiredException(String msg) {
        super(msg);

    }
}
