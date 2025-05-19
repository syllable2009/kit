package com.jxp.llm.embedding.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.jxp.llm.embedding.dto.SegmentRule;
import com.jxp.llm.embedding.service.SegmentService;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jiaxiaopeng
 * Created on 2025-05-16 21:33
 */

@Slf4j
@Service
public class SegmentServiceImpl implements SegmentService {

    @SuppressWarnings("checkstyle:MagicNumber")
    @Override
    public List<TextSegment> segment(Document document, SegmentRule rule) {
        // 分段预处理

        // 字符分割器（CharacterSplitter）块大小5000字符，重叠5字符
        DocumentSplitter splitter = DocumentSplitters.recursive(5000, 5);
        return splitter.split(document);
    }
}
