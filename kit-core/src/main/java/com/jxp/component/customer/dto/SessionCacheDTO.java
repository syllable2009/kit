package com.jxp.component.customer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jiaxiaopeng
 * Created on 2025-02-18 17:10
 */

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class SessionCacheDTO {

    private String sid;
    private String appId;
    private String userId;
    private String username;
    private String startMsgKey;
    private String endMsgKey;
    private String state;
    private long stimestamp;

    // 会话是否被锁定
    private Boolean hasLocked;
}
