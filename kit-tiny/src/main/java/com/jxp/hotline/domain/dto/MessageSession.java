package com.jxp.hotline.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 原始事件的操作
 * @author jiaxiaopeng
 * Created on 2025-03-20 10:32
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MessageSession {

    // 事件p2p=单聊 group=群聊
    private String sessionType;
    private String from;
    private String to;
    // 如果是群组id
    private String groupId;
}
