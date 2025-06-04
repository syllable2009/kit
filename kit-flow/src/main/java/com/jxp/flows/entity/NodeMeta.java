package com.jxp.flows.entity;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author jiaxiaopeng
 * Created on 2025-05-26 10:36
 */

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class NodeMeta extends BaseEntity {

    private String nodeName;

    private String nodeType;

    private Integer version; // 0版本为release版本,可以拆分成2张不同的表，数据完全分开

    // 渠道
    private List<String> channel;

    // 公开
    private String publish;

    // 组合的数据结构，json-array，表示为一个图形数据结构
    private String content;
}
