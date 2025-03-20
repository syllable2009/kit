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
    // 会话排队数量，appId：groupId
    public static String AppGroupQueueNum = "hotline:manual:queue:num:{}:{}";

    public static String format(String key, Object... params) {
        return StrUtil.format(key, params);
    }
}
