package com.jxp.flows.domain;


import com.jxp.flows.enums.NodeState;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jiaxiaopeng
 * Created on 2025-06-04 11:46
 */

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class NodeResult {

//    NodeState getState();
//
//    FlowContext getNodeContext();

    private NodeState state;

    private String message;

    private FlowContext nodeContext;


    public static NodeResult fail(FlowContext nodeContext, String message) {
        return NodeResult.builder()
                .state(NodeState.FAILED)
                .nodeContext(nodeContext)
                .message(message)
                .build();
    }

    public static NodeResult success(FlowContext nodeContext) {
        return NodeResult.builder()
                .nodeContext(nodeContext)
                .state(NodeState.COMPLETED)
                .build();
    }
}
