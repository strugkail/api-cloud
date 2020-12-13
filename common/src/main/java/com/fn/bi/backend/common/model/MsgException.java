package com.fn.bi.backend.common.model;

public class MsgException extends RuntimeException {
    public MsgException() {
        super();
    }

    public MsgException(String message) {
        super(message);
    }

    public MsgException(String message, Throwable cause) {
        super(message, cause);
    }

    public MsgException(Throwable cause) {
        super(cause);
    }

    protected MsgException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
