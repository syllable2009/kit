package com.jxp.llm.service.impl;

import org.springframework.stereotype.Service;

import com.jxp.llm.dto.LlmChatRequest;
import com.jxp.llm.dto.ModelProvider;
import com.jxp.llm.service.ChatLanguageModelFactory;

import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jiaxiaopeng
 * Created on 2025-05-12 14:12
 */
@Slf4j
@Service
public class ChatLanguageModelFactoryImpl implements ChatLanguageModelFactory {

    // 创建路由模型
    RoutingModel<ChatModel> router = new RoutingModel<>(model -> {
        if (isCodeTask(input)) return codeReviewModel;
        else return dataAnalysisModel;
    });

    @Override
    public StreamingChatLanguageModel create(LlmChatRequest request, ModelProvider provider) {
        return null;
    }
}
