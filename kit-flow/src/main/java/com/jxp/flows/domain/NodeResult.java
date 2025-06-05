package com.jxp.flows.domain;


import java.util.Collections;
import java.util.List;

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

//    private FlowContext nodeContext;

    private List<Param> output;

    public static NodeResult fail(String message) {
        return NodeResult.builder()
                .state(NodeState.FAILED)
                .output(Collections.EMPTY_LIST)
                .message(message)
                .build();
    }

    public static NodeResult success(List<Param> output) {
        return NodeResult.builder()
                .state(NodeState.COMPLETED)
                .output(output)
                .build();
    }

    public static NodeResult success() {
        return NodeResult.builder()
                .state(NodeState.COMPLETED)
                .output(Collections.EMPTY_LIST)
                .build();
    }
}
