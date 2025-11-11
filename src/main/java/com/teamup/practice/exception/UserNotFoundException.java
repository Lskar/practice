package com.teamup.practice.exception;



public class UserNotFoundException extends BaseException {
    public UserNotFoundException(String code, String message) {
        super(code, message);
    }
}
