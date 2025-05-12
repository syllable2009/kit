package com.jxp.llm.service;

import com.jxp.llm.dto.LlmChatRequest;
import com.jxp.llm.dto.ModelProvider;

import dev.langchain4j.model.chat.StreamingChatLanguageModel;

/**
 * @author jiaxiaopeng
 * Created on 2025-05-12 14:12
 */
public interface ChatLanguageModelFactory {

    StreamingChatLanguageModel create(LlmChatRequest request, ModelProvider provider);
}
