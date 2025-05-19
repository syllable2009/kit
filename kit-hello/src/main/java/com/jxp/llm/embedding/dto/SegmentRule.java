package com.jxp.llm.embedding.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jiaxiaopeng
 * Created on 2025-05-16 21:49
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SegmentRule {
    // 自动分段，自定义分段
    private String segmentType;
    // 自定义分段方式：换行 中文句号 英文句号 自定义
    private String segmentMethod;
    // 自定义分段值
    private String segmentValue;
    // 分隔最小字符
    private Integer minLen = 100;
    // 分隔最大字符
    private Integer maxLen = 400;
    // 切片重复值
    private Integer overlap = 0;
    // 替换连续符号
    private Boolean replaceConsecutiveSymbols = false;
    // 清除URL
    private Boolean removeUrl = false;
    // 清除邮箱地址
    private Boolean removeEmail = false;
}
