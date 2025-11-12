package com.teamup.practice.handler;


import com.teamup.practice.enums.HttpStatusEnum;
import com.teamup.practice.exception.BaseException;
import com.teamup.practice.result.ResponseResult;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


//@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log= LoggerFactory.getLogger(GlobalExceptionHandler.class);

    //  处理单个参数校验失败抛出的异常
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseResult<List<String>> handleConstraintViolationException(ConstraintViolationException e) {
        Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();
        List<String> collect= constraintViolations.stream().map(ConstraintViolation::getMessage).toList();
        return ResponseResult.fail(HttpStatusEnum.BAD_REQUEST.getCode(),HttpStatusEnum.BAD_REQUEST.getMessage(),collect);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseResult<List<String>> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        List<String> collect = new ArrayList<>();
        for (FieldError fieldError : fieldErrors) {
            log.error("参数 {} = {} 校验错误：{}", fieldError.getField(), fieldError.getRejectedValue(), fieldError.getDefaultMessage());
            collect.add(fieldError.getDefaultMessage());
        }
        return ResponseResult.fail(HttpStatusEnum.BAD_REQUEST.getCode(),HttpStatusEnum.BAD_REQUEST.getMessage(),collect);
    }
    //处理 form data方式调用接口校验失败抛出的异常
    @ExceptionHandler(BindException.class)
    public ResponseResult<List<String>> bindExceptionHandler(BindException e) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        List<String> collect = fieldErrors.stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toList();
        return ResponseResult.fail(HttpStatusEnum.BAD_REQUEST.getCode(),HttpStatusEnum.BAD_REQUEST.getMessage(),collect);
    }

    @ExceptionHandler(BaseException.class)
    public ResponseResult<String> handleBaseException(BaseException e) {
        log.error("业务异常：{}",e.getMessage());
        return ResponseResult.fail(e.getCode(),e.getMessage());
    }
    @ExceptionHandler(Exception.class)
    public ResponseResult<String>  handleException(Exception e){
        log.error("系统异常：{}",e.getMessage());
        return ResponseResult.fail(HttpStatusEnum.ERROR.getCode(),"出现系统异常");
    }

}
