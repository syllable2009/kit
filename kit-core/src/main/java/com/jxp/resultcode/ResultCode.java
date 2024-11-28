package com.jxp.resultcode;

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
public class ResultCode {

    private Integer code;
    private String zhCn;
    private String enUs;

    public static final Map<Integer, ResultCode> CODE_MAP = new HashMap<>();


    static {
//        Field[] fields = CommonResultCode.class.getFields();
//        Arrays.stream(fields)
//                .forEach(f -> {
//                    if (f.isAnnotationPresent(IExceptionCode.class)) {
//                        IExceptionCode myAnnotation = f.getAnnotation(IExceptionCode.class);
//                        CODE_MAP.put(myAnnotation.code(), ResultCode.builder()
//                                .code(myAnnotation.code())
//                                .zhCn(myAnnotation.zhCN())
//                                .enUs(myAnnotation.enUS())
//                                .build());
//                    }
//                });
    }

    public static ResultCode of(Integer code) {
        return CODE_MAP.getOrDefault(code,
                ResultCode.builder()
                        .code(-1)
                        .zhCn("未知")
                        .enUs("unkown")
                        .build());
    }
}
