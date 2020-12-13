package com.fn.bi.backend.common.config.core;

import com.fn.bi.backend.common.dto.Result;
import com.fn.bi.backend.common.model.MsgException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class MsgExceptionHandler {
    @ExceptionHandler(MsgException.class)
    public Result<?> msgExceptionHandler(HttpServletRequest req, MsgException msg){
        msg.getCause().printStackTrace();
        return Result.error(msg.getMessage());
    }
}
