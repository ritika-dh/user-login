package org.paf.user_login.exception;

public class RefreshTokenExpiredException extends Exception {
    public RefreshTokenExpiredException() {
        super("Refresh token has expired. Please login again");
    }
}