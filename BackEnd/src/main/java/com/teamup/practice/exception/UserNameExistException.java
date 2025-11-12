package com.teamup.practice.exception;

public class UserNameExistException extends BaseException {
    public UserNameExistException(String code, String message) {
        super(code, message);
    }
}
