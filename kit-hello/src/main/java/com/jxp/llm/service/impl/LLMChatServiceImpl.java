package com.jxp.llm.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.jxp.llm.dto.LlmChatRequest;
import com.jxp.llm.dto.LlmChatResponse;
import com.jxp.llm.service.LLMChatService;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.model.StreamingResponseHandler;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.model.output.TokenUsage;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

/**
 * @author jiaxiaopeng
 * Created on 2025-05-12 11:56
 */

@Slf4j
@Service
public class LLMChatServiceImpl implements LLMChatService {
    @Override
    public Flux<LlmChatResponse> streamChat(LlmChatRequest request) {

        // 获取model
        StreamingChatLanguageModel chatLanguageModel = null;
        //消息转换
        List<ChatMessage> messages = Lists.newArrayList();

        return Flux.create(sink -> {
            chatLanguageModel.generate(messages, new StreamingResponseHandler<AiMessage>() {

                @Override
                public void onNext(String s) {
                    LlmChatResponse chunk = new LlmChatResponse();
                    chunk.setContent(s);
                    sink.next(chunk);
                }

                @Override
                public void onComplete(Response<AiMessage> response) {
                    LlmChatResponse chunk = new LlmChatResponse();
                    chunk.setComplete(true);
                    chunk.setContent("");
                    TokenUsage tokenUsage = response.tokenUsage();
                    sink.next(chunk);
                    sink.complete();
                }

                @Override
                public void onError(Throwable throwable) {
                    sink.error(throwable);
                }
            });
        }, FluxSink.OverflowStrategy.BUFFER);
    }
}
