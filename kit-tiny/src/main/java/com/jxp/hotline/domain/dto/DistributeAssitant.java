package com.jxp.hotline.domain.dto;

import com.jxp.hotline.domain.entity.AssistantInfo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jiaxiaopeng
 * Created on 2025-03-24 10:47
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class DistributeAssitant {

    private AssistantInfo assistantInfo;

    private Boolean distributeResult;

    // 如果分配失败的原因
    private String failReason;

    // 分配策略，多了一个分配最近接待过的客服
    private String distributeStrategy;

    // 分配的sessionId
    private String sessionId;
}
