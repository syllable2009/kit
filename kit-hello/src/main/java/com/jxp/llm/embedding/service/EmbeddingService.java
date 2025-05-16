package com.jxp.llm.embedding.service;

import java.util.List;
import java.util.Map;

import com.jxp.llm.embedding.dto.EmbeddingWordResult;
import com.jxp.llm.embedding.dto.EmbeddingWordsResult;

/**
 * @author jiaxiaopeng
 * Created on 2025-05-16 16:27
 */
public interface EmbeddingService {

    EmbeddingWordResult word2Vec(String word, Map<String, String> config);

    EmbeddingWordsResult words2Vec(List<String> words, Map<String, String> config);
}
