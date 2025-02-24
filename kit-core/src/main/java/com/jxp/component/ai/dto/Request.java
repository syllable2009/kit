package com.jxp.component.ai.dto;

import java.util.Map;

import com.jxp.component.customer.dto.SessionCacheDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jiaxiaopeng
 * Created on 2025-02-21 16:48
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Request {

    private String username;
    private String userId;
    private double amount;
    private Map<String, String> extraMap;
    // 消息唯一id
    private String uuid;
    // 事件戳
    private Long timestamp;
    // 应用id
    private String appId;
    // 事件类型
    private String eventType;
    // 来源
    private String sessionType;
    // 消息抽象成str
    private String content;

    private SessionCacheDTO session;
}
