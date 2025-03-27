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

    private String appId;
    private String groupId;
    private String groupName;
    // 是否在工作时间
    private Boolean working;
    // 是否开启自动分配
    private Boolean autoDistribute;
    // 分配策略：workload=工作量，longest=最久未分配,saturation=饱和度
    private String distributeStrategy;
    // 是否分配最近接待过的客服
    private Boolean ifDistributeRecently;

    // 是否开启排队过多不接单，超过queueNum留言
    private Boolean ifRejectQueue;
    // 如果开启多少个不接单20
    private Integer rejectQueueNum;

    // 是否进行转人工确认操作
    private Boolean ifEnableConfirm;
    // 是否进行转人工确认操作的排队数10
    private Integer confirmNum;

    // 是否开启排队提醒
    private Boolean ifNoticeQueue;
    private Integer noticeManyNum;
    private Integer noticeMoreNum;
}
