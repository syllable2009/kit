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
public class CardInfo {
    private String messageKey;
    private Map<String, String> actionValue;
    private Map<String, String> globalValue;
    // 是否转发
    private Boolean forward;
    private List<String> urls;
    private List<String> messageKeys;
    // multiMedia图文混排 url 文本 图
    private String messageType;
}
