package com.jxp.setting.domain;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ddd
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SetEntity {
    /**
     * 自增
     */
    private Long aid;

    private String uid;

    // 一个应用一张表，否则使用appId来区分，应用id

    /**
     * 张三 ios端 关注的话题XXX 标记红点
     *
     * 用户id，设备id，请求id等唯一标识
     */
    private String bizId;

    // 维度：人 设备pc ios andriod 等
    private String bizType;

    // 话题 red-dot kimo-user-agreement access-time setting system-setting
    private String propertyName;

    // 代表具体属性，可为空，关注的话题id
    private String propertyId;

    // 代表操作对象标记红点
    private String propertyKey;

    // 代表值
    private String propertyValue;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private String createId;

    private String updateId;
}