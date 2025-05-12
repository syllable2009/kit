package com.jxp.llm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jiaxiaopeng
 * Created on 2025-05-12 14:06
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Usage {
    private int promptTokens;
    private int completionTokens;
    private int totalTokens;
}
