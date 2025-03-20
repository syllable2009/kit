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
public class AssistantGroupInfo {
    private String groupId;
    private String groupName;
    // 是否在工作时间
    private Boolean working;

    // 是否开启排队过多不接单
    private Boolean ifRejectManyQueue;
    // 如果开启多少个不接单
    private Integer queueNum;
}
