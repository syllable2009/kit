package com.jxp.delayevent;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

/**
 * @author jiaxiaopeng
 * Created on 2025-05-14 14:56
 */
@Data
@Builder
@ToString
public class DelayEvent {
    /**
     * 唯一标识一个事情的ID
     * 同一个uniqueId的事件只会有一个超时时间存在
     */
    private String uniqueId;

    // 事件发生的事件戳，用来校验事件的续签
    private Long timeStamp;
    /**
     * 事件类型
     */
    private DelayEventType eventType;
    /**
     * 延迟的毫秒数-相比当前时间
     */
    private Long delayMill;
    /**
     * 延迟到指定时间发送--和delayMill只有一个字段生效，优先delayMill
     */
    private Long delayToTimestamp;
    /**
     * 消息上携带的自定义额外信息
     */
    private String payload;
}
