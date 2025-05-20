package com.jxp.llm.embedding.service;

import java.io.File;

/**
 * @author jiaxiaopeng
 * Created on 2025-05-19 17:05
 */
public interface PDFContentService {
    String extractContext(String fileId, File pdfFile);
}
