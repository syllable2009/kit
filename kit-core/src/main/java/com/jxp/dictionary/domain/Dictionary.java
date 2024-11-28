package com.jxp.dictionary.domain;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jiaxiaopeng
 * Created on 2024-11-22 14:29
 */

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Dictionary {
    private Long aid;
    private String uid;
    private String nameCn; // 性别
    private String nameEn; // gender
    private String bizCode; // DictBizCodeEnum
    private String version; // 版本 1.0
    private LocalDateTime createTime;
    private LocalDateTime modifiedTime;
}
