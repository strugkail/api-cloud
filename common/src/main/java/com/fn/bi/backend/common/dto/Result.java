package com.fn.bi.backend.common.dto;

import com.fn.bi.backend.common.ienum.StatusCode;
import lombok.Data;

import java.io.Serializable;
import java.time.Instant;
import java.time.ZonedDateTime;

//@ApiModel(description = "rest请求的返回模型，所有rest正常都返回该类的对象")
@Data
public class Result<T> implements Serializable {
    private Integer statusCode;
    private String msg;
    private Instant time;
    private T data;

    public Result() {
        this.time = ZonedDateTime.now().toInstant();
    }

    private Result(Integer statusCode, String msg, T data) {
        this.statusCode = statusCode;
        this.msg = msg;
        this.time = ZonedDateTime.now().toInstant();
        this.data = data;
    }

    public static <T> Result<T> ok(T data) {
        return new Result<>(StatusCode.success.getStatusCode(), "", data);
    }

    public static <T> Result<T> ok(String msg) {
        return new Result<>(StatusCode.success.getStatusCode(), msg, null);
    }

    public static <T> Result<T> ok(String msg, T data) {
        return new Result<>(StatusCode.success.getStatusCode(), msg, data);
    }

    public static <T> Result<T> ok() {
        return new Result<>(StatusCode.success.getStatusCode(), "操作成功", null);
    }

    public static <T> Result<T> error(String msg) {
        return new Result<>(StatusCode.fail.getStatusCode(), msg, null);
    }

    public static <T> Result<T> error(StatusCode statusCode, String msg) {
        return new Result<>(statusCode.getStatusCode(), msg, null);
    }

    public static <T> Result<T> error() {
        return new Result<>(StatusCode.fail.getStatusCode(), "操作失败", null);
    }

    public static <T> Result<T> error(StatusCode statusCode, String msg, T data) {
        return new Result<>(statusCode.getStatusCode(), msg, data);
    }

    public static <T> Result<T> badArgument() {
        return new Result<>(StatusCode.badArgument.getStatusCode(), "参数不对", null);
    }


    public static <T> Result<T> unLogin() {
        return new Result<>(StatusCode.notLogin.getStatusCode(), "请登录", null);
    }

    public static <T> Result<T> updatedDataFailed() {
        return new Result<>(StatusCode.updatedDataFailed.getStatusCode(), "更新数据失败", null);
    }

    public static <T> Result<T> unAuthz() {
        return new Result<>(StatusCode.notAuthz.getStatusCode(), "无操作权限", null);
    }

    public static <T> Result<T> serious() {
        return new Result<>(StatusCode.serious.getStatusCode(), "系统内部错误", null);
    }

    public static <T> Result<T> unSupport() {
        return new Result<>(StatusCode.notSupport.getStatusCode(), "业务不支持", null);
    }

    public static <T> Result<T> updatedDateExpired() {
        return new Result<>(StatusCode.updatedDateExpired.getStatusCode(), "更新数据已经失效", null);
    }

    public static <T> Result<T> badArgumentValue() {
        return new Result<>(StatusCode.badArgumentValue.getStatusCode(), "参数值不对", null);
    }
}

