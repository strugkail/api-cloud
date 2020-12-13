package com.fn.bi.backend.common.util;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;

public class HttpUtil {

    public static Integer getUserId() {
        return Integer.valueOf(((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes()))
                .getRequest().getHeader("backend-User-Id"));
    }
}
