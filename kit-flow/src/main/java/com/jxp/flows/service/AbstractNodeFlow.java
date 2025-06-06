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
 * flow不会有返回值，组合的flow会共用一个context
 * node必须有返回值和执行痕迹，flow也有
 * @author jiaxiaopeng
 * Created on 2025-06-04 14:30
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class AbstractNodeFlow implements IFlow {
    private String name;
    private NodeTypeEnum type;

    private List<Param> input;

    private NodeResult nodeResult;

}
