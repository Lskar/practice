package com.teamup.practice.exception;

public class UserSaveErrorException extends BaseException {
    public UserSaveErrorException(String code, String message) {
        super(code,message);
    }
}
