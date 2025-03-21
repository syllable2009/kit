package com.jxp.hotline.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jiaxiaopeng
 * Created on 2025-03-20 10:28
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MessageEvent {

    private String uuid;
    private String token;
    private Long timestamp;
    // 应用号id
    private String appId;
    // info事件类型：message action recall reaction
    private String eventType;

    // 原始事件的会话类型，p2p单聊 group普通群聊 groupTag客服群聊
    private String sessionType;
    // group groupTag时的id
    private String sessionId;

    private MessageInfo info;

    // 原始事件的操作者
    private EventUser from;

    // 原始事件的接受者
    private EventUser to;

    // 本次操作者，可以对原始事件表态，撤回，回复操作的人
    private EventUser operator;

}
