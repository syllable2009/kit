package com.jxp.response;

import java.time.LocalDateTime;

import org.slf4j.MDC;

import com.jxp.resultcode.CommonResultCode;
import com.jxp.resultcode.ResultCode;
import com.jxp.tool.WebServerUtils;

/**
 * @author jiaxiaopeng
 * Created on 2024-11-28 17:21
 */

public class Result<T> {
    private Integer status;

    private String messageCn;

    private String messageEn;

    private T data;

    private String traceId;

    private String port;

    private LocalDateTime timestamp;

    // i18n的配置
    private Result(Integer status, String messageCn, String messageEn, T data) {
        this.status = status;
        this.messageCn = messageCn;
        this.messageEn = messageEn;
        this.data = data;
        this.traceId = MDC.get("ktraceId");
        this.port = Integer.toString(WebServerUtils.getPort());
        this.timestamp = LocalDateTime.now();
    }

    public Result(ResultCode resultCode) {
        this(resultCode.getCode(), resultCode.getZhCn(), resultCode.getEnUs(), null);
    }

    public Result(ResultCode resultCode, T data) {
        this(resultCode.getCode(), resultCode.getZhCn(), resultCode.getEnUs(), data);
    }

    public static <T> Result<T> ok() {
        return ok(null);
    }

    public static <T> Result<T> ok(T data) {
        return new Result<>(CommonResultCode.OK.getCode(),
                CommonResultCode.OK.getZhCn(), CommonResultCode.OK.getEnUs(), data);
    }

    public static <T> Result<T> error(ResultCode resultCode) {
        return new Result<>(resultCode);
    }

    public static Result<Boolean> errorFalse(ResultCode resultCode) {
        return new Result<>(resultCode, false);
    }

}
