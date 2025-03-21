package com.jxp.hotline.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 客服组，也是技能队列信息
 * @author jiaxiaopeng
 * Created on 2025-03-20 15:38
 */

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AssistantInfo {
    private String assistantId;
    // online offline busy
    // 只有在线online才可以自动分配，转接可以自定义状态
    private String state;
    // 应用下分配上限
    private Integer maxAppCount;
    // 全平台分配上限
    private Integer maxGlobalCount;
    private String appId;
}
