package com.jxp.hotline.domain.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jiaxiaopeng
 * Created on 2025-03-27 15:46
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ForwardSessionDTO {
    // 原始会话id
    private String sessionId;
    // 操作人
    private String operator;
    // 转接给人
    private String assitantId;
    // 转接给组
    private String groupId;
    // 是否携带历史消息
    private Boolean forwardHistory;
    // 自定义的携带的历史消息，会做合并转发
    private List<String> historyMsg;
}
