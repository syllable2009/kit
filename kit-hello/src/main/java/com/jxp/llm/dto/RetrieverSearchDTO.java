package com.jxp.llm.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 召回数据请求对象
 * @author jiaxiaopeng
 * Created on 2025-05-16 14:59
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RetrieverSearchDTO {

    private List<Long> knowledgeIds;
    private String q;
    private Integer topK = 10;
    private Double minScore = 0.5D;
    private List<Long> docIds;
    private String channel;
    private Long botId;
    private String mockName;
    private String retrieverType;
    private Boolean includeAnchor;
    private Long runId;
    private Long t = System.currentTimeMillis();
}
