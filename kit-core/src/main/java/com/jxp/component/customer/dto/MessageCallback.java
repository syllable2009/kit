package com.jxp.component.customer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jiaxiaopeng
 * Created on 2025-02-18 16:45
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class MessageCallback {

    // 消息唯一id
    private String uuid;
    // 事件戳
    private Long timestamp;
    // 应用id
    private String appId;
    // 事件类型
    private String eventType;
    // 用户id
    private String userId;
    // 来源
    private String sessionType;
    // 消息抽象成str
    private String content;
}
