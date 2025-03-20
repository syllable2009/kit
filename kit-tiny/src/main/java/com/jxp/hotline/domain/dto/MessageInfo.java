package com.jxp.hotline.domain.dto;

import java.util.List;
import java.util.Map;

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
public class MessageInfo {
    // 消息的唯一id
    private String messageKey;
    // text forword audio vedio picture multi recall reply reaction=表态
    private String messageType;
    // 文本的原始内容 @张三 你说啥呢 @李四
    private String content;
    // 文本的替换内容
    private String contentWithoutAtBot;
    // 是否at所有人
    private Boolean mentionAll;
    // metion列表
    private List<EventUser> mentionList;

    // 具体的消息类型，这里简化成string
    private EventImage image;
    private String audio;
    private String file;
    private String multi;
    private String reference;
    private String video;
    private String custom;
    private MessageReaction reaction;

    // 额外信息map的json格式
    private Map<String, String> extra;

    private String emojiId;
    private String reactionType;
}
