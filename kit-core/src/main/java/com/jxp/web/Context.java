package com.jxp.web;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author jiaxiaopeng
 * Created on 2023-06-28 11:37
 * 用户请求上下文兑现个，可以临时存放用户数据
 */
@Data
@Builder
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class Context {
    private String userId;
    private Boolean anonymous; // null标识未知类型
    private Long requestTimestamp;
    private String language;
}
