package com.jxp.llm.embedding.service;

import java.util.List;

import com.jxp.llm.embedding.dto.SegmentRule;

/**
 * @author jiaxiaopeng
 * Created on 2025-05-16 21:33
 */
public interface SegmentService {

    List<String> splitTxt(String context, SegmentRule rule);

    List<String> segment(String fileType, String fileId, SegmentRule rule);
}
