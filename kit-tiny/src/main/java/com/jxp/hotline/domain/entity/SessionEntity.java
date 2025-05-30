package com.jxp.hotline.domain.entity;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 会话信息以用户所在的targetType为主
 * @author jiaxiaopeng
 * Created on 2025-03-20 11:41
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SessionEntity {

    private Long aid;
    // 会话id，对外暴露
    private String sid;
    // 只能助理的机器人id
    private String robotId;
    // 消息号id
    private String appId;
    // bot manual
    private String sessionType;
    // -1=机器人聊天botChat，queue=排队 muanualChat=人工聊天 end=结束
    private String sessionState;
    // 如果结束，表示结束原因，客服关闭 1manualClose, 2:用户关闭userClose, 3:用户超时 userTimeout, 4:客服超时 manualTimeout 5转接关闭transfer
    // 6排队超时queueTimeout
    private String cause;
    // 用户id
    private String userId;
    // 租户id，用户的租户
    private String tenantId;
    // 服务媒介类型：groupTag
    private String targetType;
    // 服务媒介id，会话服务类型groupTag，为groupId，可为空
    private String targetId;

    // 客服组id或者技能队列id
    private String groupId;
    // 客服id
    private String assitantId;
    // 会话的额外信息只用来同步展示，不做逻辑处理，统一存储为ManualSessionExtra的json格式，防止每次表都需要新建字段
    private String extra;
    // 会话来源，用户发起userToManual/userConfirm，客服发起manualToUser，转接会话 forward
    private String sessionFrom;
    // 转接会话才有的
    private String beforeSessionId;

    // 会话的创建时间，用户可以发起，客服也可以发起会话
    private LocalDateTime createTime;

    // 会话最后一个活跃时间
    private LocalDateTime sessionLastTime;

    // 会话的结束时间
    private LocalDateTime sessionEndTime;

    // 会话开始排队时间
    private LocalDateTime sessionQueueTime;

    // 会话转人工时间
    private LocalDateTime sessionManualTime;

    // 客服认领时间，分配到客服时间
    private LocalDateTime takeOverTime;

    // 人工会话才有，表示自动分配 autoDistribute，客服领取 manualClaim
    private String sessionReceiveType;

    // 最后一次修改时间
    private LocalDateTime updateTime;

    // 会话开始消息id
    private String sessionStartMessageId;

    // 结束消息id
    private String sessionEndMessageId;

    /**
     * 用户的统计信息
     */

    // 用户开始消息id
    private String userFistMessageId;

    // 用户开始消息时间
    private LocalDateTime userFistMessageTime;

    // 用户最后一条消息id
    private String userLastMessageId;

    // 用户最后一条消息时间，可用于并发消息并发判断
    private LocalDateTime userLastMessageTime;

    // 用户的bot提问量
    private Integer userRequestRobotNum;

    // 转人工后用户的提问量
    private Integer userRequestManualNum;

    // 用户未提问人工，针对转人工之后
    private Boolean userRequest;

    /**
     * 客服的统计信息
     */

    // 客服第一条消息id，排队，客服欢迎的系统提示不算
    private String manualFirstMessageId;

    // 客服第一条回复时间
    private LocalDateTime manualFirstMessageTime;

    // 客服最后一条消息id
    private String manualLastMessageId;

    // 客服第一条回复时间
    private LocalDateTime manualLastMessageTime;

    // 转人工后客服的回复量
    private Integer manualReplyNum;

    // 客服未回复，0回复，针对人工会话
    private Boolean manualReply;
}
