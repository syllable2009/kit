package com.jxp.dictionary.domain;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jiaxiaopeng
 * Created on 2024-11-22 14:25
 */

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class DictionaryItem {
    // 自增aid
    private Long aid;
    // uuid
    private String uid;

    // 通过bizcode关联
    private String bizCode;

    // 字典的code=bizCode + 自定义code
    private String code;
    private String nameCn;
    private String nameEn;
    // 简介
    private String briefCn;
    private String briefEn;
    // 图标
    private String icon;
    private String bg;

    private String state;
    private LocalDateTime createTime;
    private LocalDateTime modifiedTime;
}
