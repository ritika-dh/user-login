package org.paf.user_login.exception;

public class InvalidTokenException extends Exception {
    public InvalidTokenException() {
        super("Access token has expired. Please login again");
    }
}
