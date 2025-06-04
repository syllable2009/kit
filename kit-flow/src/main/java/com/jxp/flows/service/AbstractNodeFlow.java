package com.jxp.flows.service;

import java.util.List;

import com.jxp.flows.domain.NodeResult;
import com.jxp.flows.domain.Param;
import com.jxp.flows.enums.NodeTypeEnum;
import com.jxp.flows.infs.IFlow;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author jiaxiaopeng
 * Created on 2025-06-04 14:30
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class AbstractNodeFlow implements IFlow {
    private String name;
    private String runId;
    private NodeTypeEnum type;

    private List<Param> input;

    private NodeResult nodeResult;

}
