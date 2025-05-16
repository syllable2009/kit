package com.jxp.llm;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jxp.llm.embedding.dto.EmbeddingWordResult;
import com.jxp.llm.embedding.dto.EmbeddingWordsResult;
import com.jxp.llm.embedding.service.EmbeddingService;

import cn.hutool.core.util.StrUtil;

/**
 * @author jiaxiaopeng
 * Created on 2025-05-16 17:14
 */

@RequestMapping("/llm")
@RestController
public class LlmApi {

    @Resource
    private EmbeddingService embeddingService;

    @GetMapping(value = "/word2Vec")
    public ResponseEntity<?> word2Vec(@RequestParam String word) {
        final EmbeddingWordResult embeddingWordResult = embeddingService.word2Vec(word, null);
        return ResponseEntity.ok(embeddingWordResult);
    }

    @GetMapping(value = "/words2Vec")
    public ResponseEntity<?> words2Vec(@RequestParam String word) {
        final String replace = StrUtil.replaceChars(word, "，", ",");
        final List<String> split = StrUtil.split(replace, ",");
        final EmbeddingWordsResult embeddingWordsResult = embeddingService.words2Vec(split, null);
        return ResponseEntity.ok(embeddingWordsResult);
    }
}
