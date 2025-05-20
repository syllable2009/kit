package com.jxp.meili.domain;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jiaxiaopeng
 * Created on 2025-05-20 11:32
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Music {

    private Long aid;

    private String uid;

    // 封面
    private String poster;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    // 标题
    private String title;
    // 描述
    private String description;
    // 流派
    private String[] genres;
    // 歌手
    private String[] singer;
}
