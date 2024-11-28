package com.jxp.paramvalid;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jiaxiaopeng
 * Created on 2023-06-16 10:23
 */

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ValidationResult<T> {

    // 是否验证通过
    private T param;
    private boolean ifValid;
    private Integer errorCode;
    private String errorMsgCn;
    private String errorMsgEn;

    public boolean notValid() {
        return !ifValid;
    }
}
