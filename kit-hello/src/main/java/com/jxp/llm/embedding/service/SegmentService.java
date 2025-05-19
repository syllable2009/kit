package com.jxp.llm.embedding.service;

import java.util.List;

import com.jxp.llm.embedding.dto.SegmentRule;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.segment.TextSegment;

/**
 * @author jiaxiaopeng
 * Created on 2025-05-16 21:33
 */
public interface SegmentService {

    List<TextSegment> segment(Document document, SegmentRule rule);
}
