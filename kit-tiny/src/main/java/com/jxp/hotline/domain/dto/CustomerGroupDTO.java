package com.jxp.hotline.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jiaxiaopeng
 * Created on 2025-03-25 16:24
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CustomerGroupDTO {
    private String groupId;
    // 不可改
    private String groupName;
    // 可自定义
    private String customDisplayName;
    private Boolean enabled;
}
