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
    // 0 1
    private int state;
    // manual robot
    private String type;
    private long stimestamp;

    // 0，1-拦截，2=锁定
    private int blockState;
}
