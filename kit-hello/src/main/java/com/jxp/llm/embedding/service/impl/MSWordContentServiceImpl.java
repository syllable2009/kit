package com.jxp.llm.embedding.service.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.converter.PicturesManager;
import org.apache.poi.hwpf.converter.WordToHtmlConverter;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.hwpf.usermodel.PictureType;
import org.apache.poi.util.XMLHelper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFPicture;
import org.apache.poi.xwpf.usermodel.XWPFPictureData;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.microsoft.ooxml.OOXMLParser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

import com.jxp.llm.embedding.service.MSWordContentService;
import com.vladsch.flexmark.html2md.converter.FlexmarkHtmlConverter;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jiaxiaopeng
 * Created on 2025-05-19 17:52
 */

@Slf4j
@Service
public class MSWordContentServiceImpl implements MSWordContentService {
    @Override
    public String extractContext(File wordFile) {
        File f = extendImageWord2007("fileId", wordFile);
        if (null == f) {
            f = extendImageWord2003("fileId", wordFile);
            return FileUtil.readString(f, StandardCharsets.UTF_8);
        } else {
            return getWordContent2007(f);
        }
    }

    public String keyWrapper(String key) {
        return "@_" + key + "_@";
    }

    private File extendImageWord2007(String fileId, File word2007File) {
        try (FileInputStream fis = new FileInputStream(word2007File);
                XWPFDocument document = new XWPFDocument(fis)) {

            for (XWPFParagraph paragraph : document.getParagraphs()) {
                List<XWPFRun> runs = paragraph.getRuns();
                for (XWPFRun run : runs) {
                    List<XWPFPicture> pictures = run.getEmbeddedPictures();
                    for (XWPFPicture picture : pictures) {
                        // Replace the picture with image ID
                        XWPFPictureData data = picture.getPictureData();
                        if (null != data) {
                            String name = data.getFileName() + "." + data.getPictureTypeEnum().getExtension();
                            String path = FileUtil.getTmpDirPath() + name;
                            File imageFile = FileUtil.file(path);
                            FileUtil.writeFromStream(new ByteArrayInputStream(data.getData()), imageFile);

                            String key = fileId + "/";
                            key += SecureUtil.md5("DigestUtils.sha256Hex(name) + System.currentTimeMillis()");
                            String imageTxt = "![图示](" + keyWrapper(key) + ")";
                            run.setText(imageTxt, 0);
                        }
                    }
                }
            }
            File outputFile = FileUtil.createTempFile();
            // Save the modified document
            try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                document.write(fos);
            }
            return outputFile;
        } catch (Throwable th) {
            log.error(th.getMessage(), th);
            return null;
        }
    }

    private File extendImageWord2003(String fileId, File word2003File) {
        try (FileInputStream fis = new FileInputStream(word2003File);
                HWPFDocument doc = new HWPFDocument(fis)) {
            // 1, doc --> doc 替换和存储图片
            WordToHtmlConverter converter = new WordToHtmlConverter(
                    DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument()
            );
            converter.setPicturesManager(new PicturesManager() {
                @Override
                public String savePicture(byte[] content, PictureType pictureType, String suggestedName,
                        float widthInches,
                        float heightInches) {

                    String name = suggestedName + "." + pictureType.getExtension();
                    String path = FileUtil.getTmpDirPath() + name;
                    File imageFile = FileUtil.file(path);
                    FileUtil.writeFromStream(new ByteArrayInputStream(content), imageFile);

                    String key = fileId + "/";
                    key += SecureUtil.md5(DigestUtils.sha256Hex(name) + System.currentTimeMillis());
                    // String imageTxt = "![图示](" + keyWrapper(key) + ")";
                    return keyWrapper(key);
                }
            });
            converter.processDocument(doc);

            // 2, doc -> html
            File htmlFile = FileUtil.createTempFile();
            Document doc2 = converter.getDocument();
            DOMSource domSource = new DOMSource(doc2);
            StreamResult streamResult = new StreamResult(htmlFile);
            Transformer serializer = XMLHelper.newTransformer();
            serializer.setOutputProperty(OutputKeys.METHOD, "html");
            serializer.transform(domSource, streamResult);

            // 3, html -> markdown
            String html = FileUtil.readString(htmlFile, StandardCharsets.UTF_8);
            // Parse HTML using Jsoup
            org.jsoup.nodes.Document document = org.jsoup.Jsoup.parse(html);
            // Process links and images
            org.jsoup.select.Elements links = document.select("a[href]");
            for (org.jsoup.nodes.Element link : links) {
                String href = link.attr("href");
                link.attr("href", href); // Ensure href attribute is preserved
            }
            org.jsoup.select.Elements images = document.select("img[src]");
            for (org.jsoup.nodes.Element img : images) {
                String src = img.attr("src");
                img.attr("src", src); // Ensure src attribute is preserved
            }
            // Convert HTML to Markdown using Flexmark
            String markdown = FlexmarkHtmlConverter.builder().build().convert(document.html());

            // Output the Markdown
            File outputFile = FileUtil.createTempFile();
            FileUtil.writeFromStream(new ByteArrayInputStream(markdown.getBytes()), outputFile);
            return outputFile;
        } catch (Throwable th) {
            log.error(th.getMessage(), th);
            return null;
        }
    }

    private String getWordContent2007(File file) {
        try (InputStream inputStream = Files.newInputStream(file.toPath())) {
            // Creating a Parser object
            // Parser autoParser = new AutoDetectParser();
            OOXMLParser parser = new OOXMLParser();

            // Creating a BodyContentHandler object to handle the content
            BodyContentHandler handler = new BodyContentHandler(-1);
            // Creating a Metadata object to hold metadata about the document
            org.apache.tika.metadata.Metadata metadata = new org.apache.tika.metadata.Metadata();

            // Parsing the document
            parser.parse(inputStream, handler, metadata, new ParseContext());

            // Closing the input stream
            inputStream.close();
            // log.info("Word Content: {}", handler);
            return handler.toString();
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
            try (FileInputStream fis = new FileInputStream(file)) {
                // 使用HWPFDocument类来处理2003版本的Word文档
                HWPFDocument document = new HWPFDocument(fis);
                WordExtractor extractor = new WordExtractor(document);
                // 提取文档内容
                // System.out.println(fileContent);
                return extractor.getText();
            } catch (Throwable ex) {
                log.error(ex.getMessage(), ex);
                return StrUtil.EMPTY;
            }
        }
    }
}
