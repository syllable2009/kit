package com.jxp.llm.embedding.service.impl;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

import com.jxp.llm.dto.PDFExtendImageComponent;
import com.jxp.llm.dto.PDFExtendTableComponent;
import com.jxp.llm.embedding.service.PDFContentService;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jiaxiaopeng
 * Created on 2025-05-19 17:05
 */
@Service
@Slf4j
public class PDFContentServiceImpl implements PDFContentService {

    @Override
    public String extractContext(String fileId, File pdfFile) {
        try {
            // 处理加密
            File pdfFile2 = setAllSecurityToBeRemoved(pdfFile);
            // 处理图片
            File outFile = imageProcess(fileId, pdfFile2);
            // 提取表格
            File outFile2 = tableProcess(outFile);
            // 提取文本
            return extractContext(outFile2);
        } catch (Throwable th) {
            log.error(th.getMessage(), th);
            return StrUtil.EMPTY;
        }
    }

    private File setAllSecurityToBeRemoved(File pdfFile) {
        try (PDDocument document = Loader.loadPDF(pdfFile)) {
            File out = FileUtil.createTempFile(".pdf", true);
            document.setAllSecurityToBeRemoved(true);
            document.save(out);
            return out;
        } catch (Throwable th) {
            log.error(th.getMessage(), th);
            return null;
        }
    }

    private File imageProcess(String fileId, File pdfFile) {
        try {
            PDFExtendImageComponent pdfEx = new PDFExtendImageComponent(pdfFile);
            pdfEx.setImageProcessor(image -> {
                File pngFile = FileUtil.createTempFile(".png", true);
                try {
                    ImageIO.write(image.getImage(), "png", pngFile);
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                    return "图";
                }
                String key = fileId + "/";
                key += SecureUtil.md5("");
                return "![图示](" + keyWrapper(key) + ")";
            });
            return pdfEx.extendImage();
        } catch (Throwable th) {
            log.error(th.getMessage(), th);
            return pdfFile;
        }
    }

    private File tableProcess(File pdfFile) {
        try {
            PDFExtendTableComponent pdfEx = new PDFExtendTableComponent(pdfFile);
            return pdfEx.tableProcess();
        } catch (Throwable th) {
            log.error(th.getMessage(), th);
            return pdfFile;
        }
    }

    private String extractContext(File pdfFile) {
        StringBuilder sb = new StringBuilder();
        try (PDDocument document = Loader.loadPDF(pdfFile)) {
            AccessPermission ap = document.getCurrentAccessPermission();
            if (!ap.canExtractContent()) {
                throw new IOException("You do not have permission to extract text");
            }

            PDFTextStripper stripper = new PDFTextStripper();

            // This example uses sorting, but in some cases it is more useful to switch it off,
            // e.g. in some files with columns where the PDF content stream respects the
            // column order.
            stripper.setSortByPosition(true);

            for (int p = 1; p <= document.getNumberOfPages(); ++p) {
                // Set the page interval to extract. If you don't, then all pages would be extracted.
                stripper.setStartPage(p);
                stripper.setEndPage(p);

                // let the magic happen
                String text = stripper.getText(document);
                sb.append(text);
                // If the extracted text is empty or gibberish, please try extracting text
                // with Adobe Reader first before asking for help. Also read the FAQ
                // on the website:
                // https://pdfbox.apache.org/2.0/faq.html#text-extraction
            }
        } catch (Throwable th) {
            log.error(th.getMessage(), th);
        }
        return sb.toString();
    }

    public String keyWrapper(String key) {
        return "@_" + key + "_@";
    }
}
