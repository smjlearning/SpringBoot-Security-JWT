package com.example.security.handler;

import com.example.security.common.domain.Result;
import com.example.security.common.domain.ResultCode;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 */
@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(Exception.class)
    public Result<?> test(Exception e){
        e.printStackTrace();
        return Result.fail(ResultCode.SERVER_ERROR.getCode(),ResultCode.SERVER_ERROR.getMessage());
    }


}
