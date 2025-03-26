package com.jxp.hotline.domain.message;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author jiaxiaopeng
 * Created on 2025-03-26 11:18
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@SuperBuilder
public abstract class SendMessageRequest {
    private String userId;
    private String username;
    private String groupId;
    private String channelId;
    private String uniqId;
    private String msgType;
    private List<String> receivers;
    private String tenantId;
    private String sender;

    public SendMessageRequest(String msgType) {
        this.msgType = msgType;
    }
}
