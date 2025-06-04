package com.jxp.flows.entity;

import java.util.List;

import com.jxp.flows.domain.Param;

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
public class NodeDetailMeta extends BaseEntity {

    // 归属于那个nodeId
    private String nodeId;

    private String nodeName;

    // node,agent,plugin,workflow,action
    private String nodeType;

    // 节点值，json对象报错
    private String nodeValue;

    // start,end,
    private String bizType;

    private String bizId;

    private String nodeDesc;

    private String owner;

    // 变量引用，上级节点，系统变量，自定义变量
    private List<Param> input;

    private List<Param> output;

    // 公开
    private String publish;
}
