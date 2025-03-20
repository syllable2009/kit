package com.jxp.hotline.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jiaxiaopeng
 * Created on 2025-03-20 10:37
 */

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class EventUser {
    // 唯一id
    private String userId;
    // zhangsan
    private String username;
    // 张三
    private String name;
    // 用户属性，bot user
    private String userType;
}
