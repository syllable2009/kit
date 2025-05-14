package com.jxp.delayevent;

/**
 * @author jiaxiaopeng
 * Created on 2025-05-14 14:48
 */
public enum DelayEventType {
    MANUAL_SESSION_TIMEOUT, // 人工会话超时，会话已结束
    ROBOT_SESSION_TIMEOUT, // 机器人会话超时，会话已结束
    QUEUE_SESSION_TIMEOUT, // 会话排队超时，排队超时请留言
    USER_ANSWER_TIMEOUT_WARN, // 用户应答告警，好长时间没有应答
    ASSISTANT_REPLAY_TIMEOUT_WARN, // 客服回复告警，好长时间没有回复客服
    USER_LEAVE_MESSAGE_TIMEOUT, // 用户留言超时
}
