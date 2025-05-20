package com.jxp.llm.dto;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.contentstream.PDFStreamEngine;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import technology.tabula.ObjectExtractor;
import technology.tabula.Page;
import technology.tabula.PageIterator;
import technology.tabula.Rectangle;
import technology.tabula.RectangularTextContainer;
import technology.tabula.Table;
import technology.tabula.TextElement;
import technology.tabula.extractors.SpreadsheetExtractionAlgorithm;

/**
 * @author jiaxiaopeng
 * Created on 2025-05-19 17:10
 */
@Slf4j
public class PDFExtendTableComponent extends PDFStreamEngine {
    private final File pdfFile;

    /**
     * Default constructor.
     */
    public PDFExtendTableComponent(File pdfFile) {
        this.pdfFile = pdfFile;
    }

    private void extractTable(Map<Integer, List<Table>> res, Map<Integer, Boolean> firstTableContinue) {
        res = (null == res) ? new HashMap<>() : res;
        firstTableContinue = (null != firstTableContinue) ? firstTableContinue : new HashMap<>();
        try (PDDocument document = Loader.loadPDF(pdfFile)) {
            SpreadsheetExtractionAlgorithm sea = new SpreadsheetExtractionAlgorithm();
            PageIterator pi = new ObjectExtractor(document).extract();

            while (pi.hasNext()) {
                // iterate over the pages of the document
                Page page = pi.next();
                List<Table> tables = sea.extract(page);
                tables = Optional.ofNullable(tables).orElse(new ArrayList<>()).stream()
                        .filter(f -> f.getColCount() > 1 && f.getRowCount() > 1)
                        .collect(Collectors.toList());

                if (CollUtil.isNotEmpty(tables)) {
                    // 第一个表格, 可能是上一页的表格的延续
                    // 而且 (0, 0 ~ pageWidth, y) 这个区域如果没有文本,
                    // 那么 就判定为上一个表格的延续
                    Table t0 = tables.get(0);
                    log.info("page {} first table t0 (x:{}, y:{})", page.getPageNumber(), t0.getX(), t0.getY());
                    List<TextElement> elements = page.getText(
                            new Rectangle(0F, 0F, (float) page.getWidth(), (float) t0.getY())
                    );
                    log.info("page {} first table t0 is int the top:{}, textSize:{})",
                            page.getPageNumber(), CollUtil.isEmpty(elements), CollUtil.size(elements));
                    firstTableContinue.put(page.getPageNumber(), CollUtil.isEmpty(elements));
                    //
                    res.put(page.getPageNumber(), tables);
                }
            }
        } catch (IOException e) {
            log.error("extractTable error: {}", e.getMessage(), e);
        }
    }

    public File tableProcess() {
        Map<Integer, List<Table>> tables = new HashMap<>();
        Map<Integer, Boolean> firstTableContinue = new HashMap<>();
        this.extractTable(tables, firstTableContinue);
        // 字体
        File tf = FileUtil.createTempFile();
        FileUtil.writeFromStream(ResourceUtil.getStream("微软雅黑.ttf"), tf);
        log.info("ft size: {}", tf.length());
        //
        try (PDDocument document = Loader.loadPDF(pdfFile)) {
            PDType0Font font = PDType0Font.load(document, tf);
            for (int i = 0; i < document.getNumberOfPages(); i++) {
                List<Table> tableList = tables.get(i + 1);
                if (CollUtil.isEmpty(tableList)) {
                    continue;
                }
                PDPage page = document.getPage(i);
                Table t0 = tableList.get(0);
                boolean successor = firstTableContinue.getOrDefault(i + 1, false);
                toHtml(document, page, font, t0, successor);
                for (int j = 1; j < tableList.size(); j++) {
                    toHtml(document, page, font, tableList.get(j), false);
                }
            }
            File out = FileUtil.createTempFile(".pdf", true);
            document.save(out);
            return out;
        } catch (Throwable th) {
            log.error(th.getMessage(), th);
            return pdfFile;
        }
    }

    @SuppressWarnings("rawtypes")
    private void toHtml(PDDocument document, PDPage page, PDType0Font font, Table table, boolean successor)
            throws IOException {
        StringBuilder all = new StringBuilder();
        List<List<RectangularTextContainer>> rows = table.getRows();
        // iterate over the rows of the table
        for (int i = 0; i < rows.size(); i++) {
            List<RectangularTextContainer> columns = rows.get(i);
            // print all column-cells of the row plus linefeed
            for (int j = 0; j < columns.size(); j++) {
                // 第一行,第一列
                StringBuilder sb = new StringBuilder();
                if (i == 0 && j == 0) {
                    if (!successor) {
                        sb.append("*table*<table>");
                    }
                }
                // 第一行
                if (j == 0) {
                    sb.append("<tr>");
                }
                //
                RectangularTextContainer cell = columns.get(j);
                // Note: Cell.getText() uses \r to concat text chunks
                String text = cell.getText().replace("\r", StrUtil.SPACE);
                if (!text.contains("<td>") && StrUtil.isNotBlank(text)) {
                    sb.append("<td>").append(text).append("</td>");
                }
                //
                // 最后一行
                if (j == columns.size() - 1) {
                    sb.append("</tr>");
                }
                // 最后一行, 最后一列
                if (i == rows.size() - 1 && j == columns.size() - 1) {
                    if (!successor) {
                        sb.append("</table>");
                    }
                }
                //
                all.append(sb);
            }
        }

        // 定义矩形区域 // x, y, width, height
        float pageHeight = page.getMediaBox().getHeight();
        PDRectangle rect = new PDRectangle(table.x, pageHeight - table.y - table.height, table.width, table.height);
        try (PDPageContentStream cs = new PDPageContentStream(document, page,
                PDPageContentStream.AppendMode.APPEND, true, true)) {
            cs.setNonStrokingColor(0.0F, 0.0F, 1.0F); // 设置填充颜色
            cs.addRect(rect.getLowerLeftX(), rect.getLowerLeftY(), rect.getWidth(), rect.getHeight());
            cs.fill();

            cs.beginText();
            cs.setFont(font, 1F);
            cs.setNonStrokingColor(1.0F, 0.0F, 0.0F); // 设置文本颜色
            cs.newLineAtOffset(rect.getLowerLeftX(), rect.getLowerLeftY());
            String extendTxt = StrUtil.replace(all.toString(), "\n", StrUtil.SPACE);
            cs.showText(extendTxt);
            cs.endText();
        }
    }
}
