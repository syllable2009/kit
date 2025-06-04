package com.jxp.llm;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.suggest.Suggester;
import com.jxp.llm.embedding.dto.EmbeddingWordResult;
import com.jxp.llm.embedding.dto.EmbeddingWordsResult;
import com.jxp.llm.embedding.service.EmbeddingService;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jiaxiaopeng
 * Created on 2025-05-16 17:14
 */
@Slf4j
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

    @GetMapping(value = "/segment")
    public ResponseEntity<?> segment(@RequestParam String word) {
        final List<Term> segment = HanLP.segment(word);
        log.info("标准分词,result:{}", JSONUtil.toJsonStr(segment));
//        final List<Term> segment1 = NLPTokenizer.segment(word);
//        log.info("NLP分词,result:{}", JSONUtil.toJsonStr(segment1));

        Suggester suggester = new Suggester();
        suggester.addSentence(word);
        log.info("suggester,result:{}", suggester.suggest("发言", 1));

        List<String> keywordList = HanLP.extractKeyword(word, 5);
        log.info("关键字提取,result:{}", keywordList);

        List<String> sentenceList = HanLP.extractSummary(word, 3);
        log.info("自动摘要,result:{}", sentenceList);

        List<String> phraseList = HanLP.extractPhrase(word, 10);
        log.info("短语提取,result:{}", phraseList);

        return ResponseEntity.ok(null);
    }

    @GetMapping(value = "/stream", produces = "text/event-stream;charset=UTF-8")
    public ResponseBodyEmitter streamLogs() {
        ResponseBodyEmitter emitter = new ResponseBodyEmitter();
        // 开启异步线程处理数据并发送
        new Thread(() -> {
            try {
                while (true) {
                    String logEntry = getLatestLogEntry();
                    if (logEntry != null) {
                        emitter.send(logEntry);
                    }
                    // 每秒检查一次日志更新
                    Thread.sleep(1000);
                }
            } catch (Exception e) {
                // 出现异常时结束响应并传递错误信息
                emitter.completeWithError(e);
            }
        }).start();

        return emitter;
    }

    private String getLatestLogEntry() {
        // 模拟从日志文件中获取最新日志条目
        return "2025-02-12 12:00:00 - INFO: User logged in successfully.";
    }
}
