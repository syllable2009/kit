package com.jxp.hotline.domain.entity;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jiaxiaopeng
 * Created on 2025-03-20 11:41
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SessionEntity {

    private Long aid;
    private String sid;
    // bot manual
    private String sessionType;
    // 0=排队 1=接入 2=结束
    private String state;
    // 如果结束，表示结束原因
    private String cause;

    // 应用号id
    private String messageServerId;
    // 租户id
    private String tenantId;
    // 会话服务类型：group
    private String targetType;
    // 会话服务id，会话服务类型group，为groupId
    private String targetId;
    // 客服id
    private String assitantId;
    // 会话的额外信息只用来同步展示，不做逻辑处理，统一存储为ManualSessionExtra的json格式，防止每次表都需要新建字段
    private String extra;
    // 会话来源，用户发起，客服发起，转接会话
    private String sessionFrom;

    private String beforeSessionId;

    // 表示自动分配，客服领取
    private String sessionReceiveType;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    /**
     * 客服认领时间
     */
    private LocalDateTime takeOverTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    // 最后一次修改时间
    private LocalDateTime updateTime;

}
