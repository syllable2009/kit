package com.jxp.llm.embedding.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.hankcs.hanlp.mining.word2vec.Vector;
import com.hankcs.hanlp.mining.word2vec.WordVectorModel;
import com.jxp.llm.embedding.dto.EmbeddingWordResult;
import com.jxp.llm.embedding.dto.EmbeddingWordsResult;
import com.jxp.llm.embedding.service.EmbeddingService;

import lombok.extern.slf4j.Slf4j;

/**
 * 这里默认采用hanlp实现，也可以自定义不同的
 * @author jiaxiaopeng
 * Created on 2025-05-16 16:35
 */

@Slf4j
@Service
public class EmbeddingServiceImpl implements EmbeddingService {


    private static final String MODEL_FILE_NAME = "/Users/jiaxiaopeng/polyglot-zh/polyglot-zh.txt";
    @SuppressWarnings("checkstyle:ConstantName")
    private static final WordVectorModel wordVectorModel = loadModel();

    static WordVectorModel loadModel() {
        try {
            final WordVectorModel model = new WordVectorModel(MODEL_FILE_NAME);
            log.info("EmbeddingService loadModel success,model:{}", model);
            return model;
        } catch (Exception e) {
            log.error("EmbeddingService loadModel error,语料不存在，请阅读文档了解语料获取与格式：https://github.com/hankcs/HanLP/wiki/word2vec,", e);
            return null;
        }
    }

    @Override
    public EmbeddingWordResult word2Vec(String word, Map<String, String> config) {
        final Vector vector = wordVectorModel.vector(word);
        log.info("word2Vec result,word:{},vector:{}", word, vector.getElementArray());
        return null;
    }

    @Override
    public EmbeddingWordsResult words2Vec(List<String> words, Map<String, String> config) {
        return null;
    }
}
