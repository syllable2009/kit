package com.jxp.component.flow.dto.node;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jiaxiaopeng
 * Created on 2025-03-07 09:56
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class WorkFlow {
    // wokflow基本信息
    private String id;
    private String name;
    private String state;
    private String createId;
    private LocalDateTime createTime;
    private String updateId;
    private LocalDateTime updateTime;

    private WorkNode<Void> root;
}
