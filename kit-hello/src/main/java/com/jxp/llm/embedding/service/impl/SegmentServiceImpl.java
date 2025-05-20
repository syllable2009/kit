package com.jxp.llm.embedding.service.impl;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.jxp.llm.embedding.dto.SegmentRule;
import com.jxp.llm.embedding.service.MSWordContentService;
import com.jxp.llm.embedding.service.PDFContentService;
import com.jxp.llm.embedding.service.SegmentService;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.PatternPool;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
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

    @Resource
    private MSWordContentService msWordContentService;
    @Resource
    private PDFContentService pdfContentService;

    private ObjectMapper objectMapper = new ObjectMapper();


    @SuppressWarnings({"checkstyle:MethodLength", "checkstyle:LineLength", "checkstyle:MagicNumber"})
    public static void main(String[] args) {
        String str = "我是424@126.com\n\n,,,,,测试代码块ERROR\t\\t\\t2025-05-19\\n\\n\\nFile not found。。..";

        final SegmentRule rule = SegmentRule.builder()
                .maxLen(5000)
                .minLen(5)
                .overlap(0)
                .removeEmail(true)
                .removeUrl(true)
                .replaceConsecutiveSymbols(true)
                .segmentType("self")
                .build();

        final String preHandleStr = contextProcessBeforeSplit(str, rule);
        log.info("origin:{}", str);
        log.info("preStr:{}", preHandleStr);


        List<String> textList;
        // 获取分隔符
        final List<String> characters = rule.getCharacters();

        if (CollUtil.isNotEmpty(characters)) {
            // 默认分段方式
            String regexp = String.join("|", Lists.newArrayList("\\n", "\\.", "。"));
            textList = Arrays.asList(preHandleStr.split("(" + regexp + ")"));
            log.info("regexp:{}", JSONUtil.toJsonStr(textList));
        } else {
            // langchain4j分段
            Document document =
                    Document.from(preHandleStr);
            DocumentSplitter splitter = DocumentSplitters.recursive(5000, 0);
            final List<TextSegment> split = splitter.split(document);
            textList = Optional.ofNullable(split).orElse(new ArrayList<>()).stream()
                    .map(TextSegment::text)
                    .collect(Collectors.toList());
            log.info("splitter:{}", JSONUtil.toJsonStr(textList));
            textList = merge(textList, rule);
        }
    }

    private static String contextProcessBeforeSplit(String context, SegmentRule rule) {
        // 剔除邮箱
        if (rule.getRemoveEmail()) {
            context = ReUtil.delAll(PatternPool.EMAIL, context);
        } // 剔除URL
        if (rule.getRemoveUrl()) {
            context = ReUtil.delAll(PatternPool.URL_HTTP, context);
        }
        // 替换连续标点符号
        if (rule.getReplaceConsecutiveSymbols()) {
            context = reducePunctuation(context);
        }
        // 替换一些特殊字符
        // context = context.replaceAll("\u0000", ""); // removes NUL chars
        context = replaceConsecutiveInvisibleChars(context);
        return context;
    }

    // 使用正则表达式匹配连续的标点符号，并替换为单个标点符号
    // 仅压缩重复标点（保留单个）
    private static String reducePunctuation(String text) {
        return text.replaceAll("(\\p{Punct})\\1+", "$1");
    }

    // 使用正则表达式匹配连续的不可见符号，并替换为单个特定符号
    // \\p{C} 是Unicode属性匹配，表示所有控制字符（包括换行符、制表符等不可见字符）
    private static String replaceConsecutiveInvisibleChars(String text) {
        return text.replaceAll("(\\p{C})\\1+", "$1");
    }

    private static List<String> merge(List<String> segments, SegmentRule rule) {
        if (CollUtil.isEmpty(segments)) {
            return new ArrayList<>();
        }
        int min = rule.getMinLen();
        int max = rule.getMaxLen();
        int overlap = rule.getOverlap();
        List<String> rs = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        for (String seg : segments) {
            if (sb.length() < min) {
                sb.append(seg);
            } else {
                int sz = sb.length();
                String segOne = sb.toString();
                if (sz <= max) {
                    rs.add(segOne);
                    int over = sz - overlap;
                    String left = StrUtil.subSuf(sb.toString(), over);
                    sb = new StringBuilder(left);
                } else {
                    rs.add(segOne.substring(0, max));
                    int over = max - overlap;
                    String left = StrUtil.subSuf(sb.toString(), over);
                    sb = new StringBuilder(left);
                }
                sb.append(seg);
            }
        }
        if (sb.length() > 0) {
            rs.addAll(splitWithOverlap(sb.toString(), max, overlap));
        }
        return rs;
    }

    private static List<String> splitWithOverlap(String text, int len, int overlap) {
        List<String> rs = new ArrayList<>();
        while (text.length() > len) {
            int sz = text.length();
            rs.add(text.substring(0, len));
            int over = Math.min(len, sz - overlap);
            text = (text.substring(over));
        }
        if (!text.isEmpty()) {
            rs.add(text);
        }
        return rs;
    }


    @Override
    public List<String> segment(String fileType, String fileId, SegmentRule rule) {

        File file = null;
        List<String> textList = List.of();
        String context = null;
//        File file = resourceService.getFile(fileId);
        switch (fileType) {
            case "word":
                context = msWordContentService.extractContext(file);
                textList = splitTxt(context, rule);
                break;
            case "excel":
                break;
            case "pdf":
                context = pdfContentService.extractContext("fileId", file);
                textList = splitTxt(context, rule);
                break;
            case "txt":
                context = getTxtContent(file);
                textList = splitTxt(context, rule);
                break;
            case "jsonArray":
                context = getTxtContent(file);
                // 如果是 JSON_ARRAY 类型的结构化文档, 这个字段存储的是需要向量化的多个字段名称, 格式: ["a", "b.c"]
                String fields = null;
                Map<String, List<String>> kv = splitJsonArray(context, fields);
                for (String k : kv.keySet()) {
                    for (String v : kv.get(k)) {
                        textList.add(k);
                    }
                }
                break;
            default:
                break;
        }
        return textList;
    }

    private String getTxtContent(File file) {
        String content = "";
        try {
            content = FileUtil.readString(file, Charset.defaultCharset());
        } catch (Throwable th) {
            log.info("getTxtContent error {}", th.getMessage());
        }
        return content;
    }


    /**
     * 非结构化的文本分段
     */
    @Override
    public List<String> splitTxt(String context, SegmentRule rule) {
        if (StrUtil.isBlank(context)) {
            return new ArrayList<>();
        }
        List<String> textList;
        context = contextProcessBeforeSplit(context, rule);
//        默认的分隔符
//        characters = Lists.newArrayList("\\n", "\\.", "。");
        // 分割文档
        if (CollUtil.isNotEmpty(rule.getCharacters())) {
            String regexp = String.join("|", rule.getCharacters());
            textList = Arrays.asList(context.split("(" + regexp + ")"));
            textList = merge(textList, rule);
        } else {
            // load
            Document document = Document.from(context);
            // split: 如果配置了字符串, 按配置字符串分隔; 否则使用默认分隔.
            DocumentSplitter splitter = DocumentSplitters.recursive(rule.getMaxLen(), rule.getOverlap());
            List<TextSegment> segments = splitter.split(document);
            textList = Optional.ofNullable(segments).orElse(new ArrayList<>()).stream()
                    .map(TextSegment::text)
                    .collect(Collectors.toList());
            textList = merge(textList, rule);
        }
        return textList;
    }

    public Map<String, List<String>> splitJsonArray(String jsonArray, String keyPathsStr) {
        Map<String, List<String>> map = new LinkedHashMap<>();
        try {
            JsonNode rootNode = objectMapper.readTree(jsonArray);
            List<String> keyPaths = StrUtil.isBlank(keyPathsStr)
                    ? null
                    : JSONUtil.toList(keyPathsStr, String.class);
            for (JsonNode node : rootNode) {
                StringBuilder sb = new StringBuilder();
                if (null == keyPaths) {
                    sb.append(node.toString());
                } else {
                    for (String keyPath : keyPaths) {
                        String val = getString(node, keyPath);
                        if (StrUtil.isNotBlank(val)) {
                            sb.append(val).append(System.lineSeparator());
                        }
                    }
                }
                if (sb.length() > 0) {
                    map.putIfAbsent(sb.toString(), new ArrayList<>());
                    map.get(sb.toString()).add(node.toString());
                }
            }
            return map;
        } catch (Throwable th) {
            log.error("splitJsonArray error: {}", jsonArray);
            log.error(th.getMessage(), th);
            return map;
        }
    }

    /**
     * 从 JsonNode 结构中, 提取 keyPath 的值
     */
    public static String getString(JsonNode node, String keyPath) {
        String[] keys = keyPath.split("\\.");
        String val = null;
        JsonNode currentNode = node;
        for (String key : keys) {
            if (currentNode.has(key)) {
                currentNode = currentNode.get(key);
            } else {
                currentNode = null;
                break;
            }
        }
        if (currentNode != null) {
            val = currentNode.isValueNode() ? currentNode.asText() : currentNode.toString();
        }
        return val;
    }
}
