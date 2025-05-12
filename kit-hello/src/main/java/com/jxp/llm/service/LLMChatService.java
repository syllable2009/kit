package com.jxp.llm.service;

import com.jxp.llm.dto.LlmChatRequest;
import com.jxp.llm.dto.LlmChatResponse;

import reactor.core.publisher.Flux;

/**
 * @author jiaxiaopeng
 * Created on 2025-05-12 11:56
 */
public interface LLMChatService {
    Flux<LlmChatResponse> streamChat(LlmChatRequest request);
}
