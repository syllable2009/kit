package com.jxp.exception;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jxp
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Slf4j
public class ErrorCode {

    private int code;
    private String zhCn;
    private String enUs;

    public ErrorCode ErrorCode(int code, String descCn, String descEn) {
        return new ErrorCode(code, descCn, descEn);
    }

    public static final Map<Integer, ErrorCode> CODE_MAP = new HashMap<>();

    static {
        Field[] fields = CommonExceptionCode.class.getFields();

        Arrays.stream(fields)
                .forEach(f -> {
                    if (f.isAnnotationPresent(IExceptionCode.class)) {
                        IExceptionCode myAnnotation = f.getAnnotation(IExceptionCode.class);
                        CODE_MAP.put(myAnnotation.code(), ErrorCode.builder()
                                .code(myAnnotation.code())
                                .zhCn(myAnnotation.zhCN())
                                .enUs(myAnnotation.enUS())
                                .build());
                    }
                });
    }

    public static ErrorCode of(Integer code) {
        return CODE_MAP.getOrDefault(code,
                ErrorCode.builder()
                        .code(-1)
                        .zhCn("未知")
                        .enUs("unkown")
                        .build());
    }
}
