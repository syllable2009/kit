package com.jxp.llm.embedding.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jiaxiaopeng
 * Created on 2025-05-16 16:29
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class EmbeddingWordResult {
    private String word;
    private Float vector;
    private String embeddingType;
    // 是否成功
    private Boolean result;
    // 失败消息
    private String message;
}
