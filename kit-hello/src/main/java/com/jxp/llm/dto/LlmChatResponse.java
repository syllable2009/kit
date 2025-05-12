package com.jxp.llm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jiaxiaopeng
 * Created on 2025-05-12 11:53
 */

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class LlmChatResponse {

    /**
     * 本轮对话的id
     */
    private String id;
    /**
     * 会话id，同一个会话下多轮对话的id是相同的.
     * 请求时若未传, 会生成新的sessionId, 若传了, 则返回传的sessionId.
     */
    private String sessionId;
    /**
     * 时间戳, 单位ms
     */
    private long created;
    /**
     * 表示当前子句的序号，从0开始
     */
    private int index;
    /**
     * 对话返回内容
     */
    private String content;
    /**
     * 表示当前子句是否是最后一句
     */
    private boolean complete;
    /**
     * token统计信息
     */
    private Usage usage;
}
