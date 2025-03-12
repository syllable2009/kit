package com.jxp.component.flow.dto.node;

import java.util.Map;

/**
 * @author jiaxiaopeng
 * Created on 2025-03-07 10:37
 */
public interface WorkNode<T> {
    // 执行逻辑
    T execute(Map<String, Object> inputs);

    default WorkNode getNext() {
        return null;
    }

    default String getName() {
        return null;
    }

    default String getId() {
        return null;
    }

    // 节点分类：节点 agent 插件 工作流 执行动作共五类
    default String getCatogory() {
        return "";
    }

    // 分类下的小类型：例如 节点下的大模型，IF_ELSE
    default String getType() {
        return "";
    }

//    // 业务id：例如 大模型id，一般放json中
//    default String getBizId() {
//        return "";
//    }
}
