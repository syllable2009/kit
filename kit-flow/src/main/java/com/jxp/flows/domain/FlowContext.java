package com.jxp.flows.domain;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;

import com.jxp.flows.infs.INode;

import cn.hutool.json.JSONUtil;
import lombok.Data;

/**
 * @author jiaxiaopeng
 * Created on 2025-06-04 11:47
 */
@Data
public class FlowContext {

    // 全局参数或者自定义参数
    private Map<String, Param> globalMap;
    // 节点执行结果参数
    private Map<String, INode> executeMap;
    // 初始入参
    private Map<String, Param> input;
    // 最终结果
    private List<Param> output;
    // 运行id
    private String runId;

    public void putGlobalParam(String key, Param value) {
        this.globalMap.put(key, value);
    }

    public Param getGlobalParam(String key) {
        return this.globalMap.get(key);
    }

    public void putExecuteNode(String key, INode value) {
        this.executeMap.put(key, value);
    }

    public INode getExecuteNode(String key) {
        return this.executeMap.get(key);
    }

    public String toString() {
        return JSONUtil.toJsonStr(this);
    }

    public static FlowContext builder() {
        return new FlowContext();
    }

    public FlowContext build() {
        if (null == this.getGlobalMap()) {
            this.setGlobalMap(new ConcurrentHashMap());
        }
        if (null == this.getExecuteMap()) {
            this.setExecuteMap(new LinkedHashMap<>());
        }
        if (null == this.getInput()) {
            this.setInput(new HashMap<>());
        }
        if (null == this.getOutput()) {
            this.setOutput(Collections.EMPTY_LIST);
        }
        if (StringUtils.isBlank(this.getRunId())) {
            this.setRunId(UUID.randomUUID().toString());
        }
        return this;
    }

}
