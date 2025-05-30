package com.jxp.hotline.constant;

import cn.hutool.core.util.StrUtil;

/**
 * @author jiaxiaopeng
 * Created on 2025-03-20 16:17
 */
@SuppressWarnings("checkstyle:VisibilityModifier")
public class SessionLockKey {

    // 会话创建或者升级使用的key appId:userId
    public static String sessionLockKey = "hotline:session:{}:{}";
    // app打招呼缓存key appId：userId
    public static String welcomeSendKey = "hotline:welcome:{}:{}";
    // 组操作的加锁key appId：groupId
    public static String sessionGroupLockKey = "hotline:session:group:{}:{}";
    // 会话排队数量，appId：groupId
    public static String AppGroupQueueNum = "hotline:manual:queue:num:{}:{}";
    // 会话排队列表
    public static String AppGroupQueueList = "hotline:manual:queue:list:{}:{}";

    // 客服全局会话总数，assitantId
    public static String AssitantGlobelSessionNum = "hotline:globel:session:num:{}";
    // 客服单应用会话数，appId：assitantId
    public static String AssitantAppSessionNum = "hotline:app:session:num:{}:{}";
    // 应用号转人工规则缓存
    public static String appTransferRule = "hotline:app:transfer:rule:{}";

    public static String format(String key, Object... params) {
        return StrUtil.format(key, params);
    }
}
