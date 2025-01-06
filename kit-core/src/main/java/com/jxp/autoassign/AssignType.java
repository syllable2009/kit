package com.jxp.autoassign;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author jiaxiaopeng
 * Created on 2025-01-06 15:18
 */

@NoArgsConstructor
@AllArgsConstructor
@Getter
public enum AssignType {

    /**
     * 饱和度，饱和度的计算方式：当前的会话数量/可接单的上限会话
     */
    SATURABILITY(0, "饱和度"),

    /**
     * 最久未分配
     */
    LONGEST(1, "最久未分配"),

    LAST(2, "上次"),

    /**
     * 工作量
     */
    WORKLOAD(3, "工作量");


    private Integer code;
    private String name;
}
